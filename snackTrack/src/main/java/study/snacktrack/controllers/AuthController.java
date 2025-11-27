package study.snacktrack.controllers;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import study.snacktrack.dto.LoginRequest;
import study.snacktrack.dto.LoginResponse;
import study.snacktrack.dto.RegisterRequest;
import study.snacktrack.entities.User;
import study.snacktrack.entities.VerificationToken;
import study.snacktrack.entities.enums.Status;
import study.snacktrack.repositories.BodyParametersRepository;
import study.snacktrack.repositories.UserRepository;
import study.snacktrack.repositories.VerificationTokenRepository;
import study.snacktrack.services.EmailService;
import study.snacktrack.services.JwtService;

/**
 * REST controller handling user registration, account activation, and login processes.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    /** Repository for accessing User entity data. */
    private final UserRepository userRepository;
    /** Utility for encoding and verifying passwords. */
    private final PasswordEncoder passwordEncoder;
    /** Service for generating JWT tokens. */
    private final JwtService jwtService;
    /** Repository for managing account verification tokens. */
    private final VerificationTokenRepository tokenRepository;
    /** Service for sending transactional emails. */
    private final EmailService emailService;
    /** Repository for checking if the user has completed their initial body parameters setup. */
    private final BodyParametersRepository bodyParametersRepository;

    /**
     * Registers a new user, saves their data, generates a verification token, and sends an activation email.
     *
     * @param request The RegisterRequest DTO containing user details.
     * @return ResponseEntity with a success message or a bad request if the email is taken.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already taken");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setSurname(request.getSurname());
        user.setStatus(Status.inactive);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        tokenRepository.save(verificationToken);

        String activationLink = "http://localhost:8080/auth/activate?token=" + token;
        emailService.sendEmail(
                user.getEmail(),
                "Activate your SnackTrack account",
                "Hi " + user.getName() + ",\n\nClick here to activate your account:\n" + activationLink);

        return ResponseEntity.ok("User registered successfully! Check your email for activation link.");
    }

    /**
     * Activates a user account using a verification token provided in the URL parameter.
     *
     * @param token The verification token string.
     * @return ResponseEntity with a success message or a bad request if the token is invalid or expired.
     */
    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam("token") String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid activation token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token expired");
        }

        User user = verificationToken.getUser();
        user.setStatus(Status.active);
        userRepository.save(user);

        tokenRepository.delete(verificationToken);

        return ResponseEntity.ok("Account activated successfully!");
    }

    /**
     * Authenticates a user using email and password.
     * Checks for inactive or banned status before issuing a JWT token.
     *
     * @param request The LoginRequest DTO containing credentials.
     * @return ResponseEntity containing a JWT token, a first-login flag, and status information.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, false, "Invalid credentials"));
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, false, "Invalid credentials"));
        }

        if (user.getStatus() == Status.inactive) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new LoginResponse(null, false, "Account not activated"));
        }

        if (user.getStatus() == Status.banned) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new LoginResponse(null, false, "Account has been banned"));
        }

        String token = jwtService.generateToken(user.getEmail(), "USER");

        boolean isFirstLogin = !bodyParametersRepository.findByUserId(user.getId()).isPresent();

        return ResponseEntity.ok(new LoginResponse(token, isFirstLogin, null));
    }

}