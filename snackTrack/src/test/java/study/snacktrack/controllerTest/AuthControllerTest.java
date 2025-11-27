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

/**
 * Unit tests for the AuthController, focusing on the core user authentication flows: registration, account activation, and login.
 * This class uses Mockito to isolate the controller logic, ensuring tests verify business rules and proper dependency interaction.
 */
class AuthControllerTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private VerificationTokenRepository tokenRepository;
    private EmailService emailService;
    private BodyParametersRepository bodyParametersRepository;

    private AuthController controller;

    /**
     * Sets up the necessary mock dependencies and initializes the AuthController instance before each test execution.
     * This uses mock objects to control the behavior of external services and repositories, guaranteeing test isolation.
     */
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

    /**
     * Tests registration failure when the provided email address is already present in the database.
     * It verifies that the controller returns an HTTP 400 Bad Request status and avoids saving any new user entity.
     */
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

    /**
     * Tests successful user registration when the email address is unique.
     * It verifies that a user is saved, a verification token is created, and an email is dispatched for activation.
     */
    @Test
    void register_shouldSaveUserAndSendEmail_whenEmailFree() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@example.com");
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

    /**
     * Tests account activation failure when the provided token has already expired.
     * It verifies that the controller returns an HTTP 400 Bad Request status with the specific "Token expired" message.
     */
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

    /**
     * Tests successful account activation using a valid, non-expired token.
     * It asserts that the user's status is changed to 'active', the user is saved, and the token is deleted from the repository.
     */
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

    /**
     * Tests activation failure when no matching verification token is found in the repository.
     * It asserts that the controller throws a {@code RuntimeException} indicating the token was missing.
     */
    @Test
    void activateAccount_shouldThrow_whenTokenNotFound() {
        when(tokenRepository.findByToken("missing")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> controller.activateAccount("missing"));
    }

    /**
     * Tests login failure when no user can be found with the provided email address.
     * It verifies that the controller returns an HTTP 401 Unauthorized status with the "Invalid credentials" message.
     */
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

    /**
     * Tests login failure when the user's account has not yet been activated.
     * It verifies that the controller returns an HTTP 403 Forbidden status with the "Account not activated" message.
     */
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

    /**
     * Tests successful login for an active user who has not yet submitted body parameters.
     * It verifies that the controller returns an HTTP 200 OK status, a JWT, and sets the {@code showSurvey} flag to true.
     */
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
        assertTrue(response.getBody().isShowSurvey());
        assertNull(response.getBody().getMessage());
    }

    /**
     * Tests successful login for an active user who has already submitted their body parameters.
     * It verifies that the controller returns an HTTP 200 OK status, a JWT, and sets the {@code showSurvey} flag to false.
     */
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
        assertFalse(response.getBody().isShowSurvey());
    }
}