package study.snacktrack.controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import study.snacktrack.controllers.AuthController;
import study.snacktrack.dto.LoginRequest;
import study.snacktrack.dto.LoginResponse;
import study.snacktrack.dto.RegisterRequest;
import study.snacktrack.entities.enums.Status;
import study.snacktrack.entities.User;
import study.snacktrack.entities.VerificationToken;
import study.snacktrack.repositories.BodyParametersRepository;
import study.snacktrack.repositories.UserRepository;
import study.snacktrack.repositories.VerificationTokenRepository;
import study.snacktrack.services.EmailService;
import study.snacktrack.services.JwtService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private VerificationTokenRepository tokenRepository;
    private EmailService emailService;
    private BodyParametersRepository bodyParametersRepository;

    private AuthController controller;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        tokenRepository = mock(VerificationTokenRepository.class);
        emailService = mock(EmailService.class);
        bodyParametersRepository = mock(BodyParametersRepository.class);

        controller = new AuthController(
                userRepository,
                passwordEncoder,
                jwtService,
                tokenRepository,
                emailService,
                bodyParametersRepository
        );
    }

    @Test
    void register_shouldReturnBadRequest_whenEmailTaken() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("taken@example.com");
        req.setPassword("secret");
        req.setName("John");
        req.setSurname("Doe");

        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        ResponseEntity<String> response = controller.register(req);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email already taken", response.getBody());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_shouldSaveUserAndSendEmail_whenEmailFree() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@example.com");   // <- poprawione, spójne z verify
        req.setPassword("secret");
        req.setName("John");
        req.setSurname("Doe");

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("hashed");

        ResponseEntity<String> response = controller.register(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("User registered successfully"));
        verify(userRepository).save(any(User.class));
        verify(tokenRepository).save(any(VerificationToken.class));
        verify(emailService).sendEmail(eq("new@example.com"), anyString(), contains("/auth/activate?token="));
    }


    // ---------------- ACTIVATE ----------------

    @Test
    void activateAccount_shouldReturnBadRequest_whenTokenExpired() {
        VerificationToken vt = new VerificationToken();
        vt.setToken("expired");
        vt.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        vt.setUser(new User());

        when(tokenRepository.findByToken("expired")).thenReturn(Optional.of(vt));

        ResponseEntity<String> response = controller.activateAccount("expired");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Token expired", response.getBody());
    }

    @Test
    void activateAccount_shouldActivateUser_whenValidToken() {
        User user = new User();
        user.setStatus(Status.inactive);

        VerificationToken vt = new VerificationToken();
        vt.setToken("valid");
        vt.setExpiryDate(LocalDateTime.now().plusHours(1));
        vt.setUser(user);

        when(tokenRepository.findByToken("valid")).thenReturn(Optional.of(vt));

        ResponseEntity<String> response = controller.activateAccount("valid");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Account activated successfully!", response.getBody());
        assertEquals(Status.active, user.getStatus());
        verify(userRepository).save(user);
        verify(tokenRepository).delete(vt);
    }

    @Test
    void activateAccount_shouldThrow_whenTokenNotFound() {
        when(tokenRepository.findByToken("missing")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> controller.activateAccount("missing"));
    }
    @Test
    void login_shouldReturnUnauthorized_whenUserNotFound() {
        LoginRequest req = new LoginRequest();
        req.setEmail("missing@example.com");
        req.setPassword("secret");

        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        ResponseEntity<LoginResponse> response = controller.login(req);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody().getMessage());
        assertNull(response.getBody().getToken());
        assertFalse(response.getBody().isShowSurvey());
    }

    @Test
    void login_shouldReturnForbidden_whenInactive() {
        User user = new User();
        user.setEmail("inactive@example.com");
        user.setPassword("hashed");
        user.setStatus(Status.inactive);

        LoginRequest req = new LoginRequest();
        req.setEmail("inactive@example.com");
        req.setPassword("secret");

        when(userRepository.findByEmail("inactive@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "hashed")).thenReturn(true);

        ResponseEntity<LoginResponse> response = controller.login(req);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Account not activated", response.getBody().getMessage());
    }

    @Test
    void login_shouldReturnOk_withTokenAndShowSurveyTrue_whenNoBodyParams() {
        User user = new User();
        user.setId(1);
        user.setEmail("valid@example.com");
        user.setPassword("hashed");
        user.setStatus(Status.active);

        LoginRequest req = new LoginRequest();
        req.setEmail("valid@example.com");
        req.setPassword("secret");

        when(userRepository.findByEmail("valid@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "hashed")).thenReturn(true);
        when(jwtService.generateToken("valid@example.com", "USER")).thenReturn("jwt-token");
        when(bodyParametersRepository.findByUserId(1)).thenReturn(Optional.empty());

        ResponseEntity<LoginResponse> response = controller.login(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt-token", response.getBody().getToken());
        assertTrue(response.getBody().isShowSurvey());  // <- poprawione
        assertNull(response.getBody().getMessage());
    }

    @Test
    void login_shouldReturnOk_withTokenAndShowSurveyFalse_whenBodyParamsExist() {
        User user = new User();
        user.setId(2);
        user.setEmail("user2@example.com");
        user.setPassword("hashed");
        user.setStatus(Status.active);

        LoginRequest req = new LoginRequest();
        req.setEmail("user2@example.com");
        req.setPassword("secret");

        when(userRepository.findByEmail("user2@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "hashed")).thenReturn(true);
        when(jwtService.generateToken("user2@example.com", "USER")).thenReturn("jwt-token-2");
        when(bodyParametersRepository.findByUserId(2)).thenReturn(Optional.of(new study.snacktrack.entities.BodyParameters()));

        ResponseEntity<LoginResponse> response = controller.login(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt-token-2", response.getBody().getToken());
        assertFalse(response.getBody().isShowSurvey());  // <- poprawione
    }
}
