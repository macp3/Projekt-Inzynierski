package study.snacktrack.controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import study.snacktrack.controllers.AdminAuthController;
import study.snacktrack.dto.LoginRequest;
import study.snacktrack.entities.Admin;
import study.snacktrack.repositories.AdminRepository;
import study.snacktrack.services.JwtService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AdminAuthController class, focusing on the administration login functionality.
 * This class uses Mockito to isolate the controller logic from its dependencies, ensuring only the controller's behavior is tested.
 */
@ExtendWith(MockitoExtension.class)
class AdminAuthControllerTest {

    @InjectMocks
    private AdminAuthController adminAuthController;

    @Mock
    private AdminRepository adminRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    private LoginRequest loginRequest;
    private Admin mockAdmin;
    private final String MOCK_PASSWORD = "rawPassword";
    private final String ENCODED_PASSWORD = "encodedPassword";
    private final String MOCK_EMAIL = "admin@test.pl";
    private final String MOCK_TOKEN = "mocked.jwt.token";

    /**
     * Sets up the common objects and mock data used for the login tests before each test method runs.
     * This prepares the {@code LoginRequest} DTO and the mock {@code Admin} entity for subsequent testing scenarios.
     */
    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail(MOCK_EMAIL);
        loginRequest.setPassword(MOCK_PASSWORD);

        mockAdmin = new Admin();
        mockAdmin.setEmail(MOCK_EMAIL);
        mockAdmin.setPassword(ENCODED_PASSWORD);
    }

    /**
     * Tests a successful admin login scenario with correct credentials.
     * It verifies that the controller returns an HTTP 200 OK status code and a valid JWT in the response body.
     */
    @Test
    void login_Success() {
        when(adminRepository.findByEmail(MOCK_EMAIL)).thenReturn(Optional.of(mockAdmin));
        when(passwordEncoder.matches(MOCK_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(jwtService.generateToken(MOCK_EMAIL, "ADMIN")).thenReturn(MOCK_TOKEN);

        ResponseEntity<String> response = adminAuthController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_TOKEN, response.getBody());

        verify(adminRepository).findByEmail(MOCK_EMAIL);
        verify(passwordEncoder).matches(MOCK_PASSWORD, ENCODED_PASSWORD);
        verify(jwtService).generateToken(MOCK_EMAIL, "ADMIN");
    }

    /**
     * Tests the scenario where the admin user requested for login is not found in the repository.
     * It asserts that a {@code RuntimeException} is thrown with the specific message "Admin not found".
     */
    @Test
    void login_AdminNotFound_ThrowsRuntimeException() {
        when(adminRepository.findByEmail(MOCK_EMAIL)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminAuthController.login(loginRequest);
        });

        assertEquals("Admin not found", exception.getMessage());
        verify(adminRepository).findByEmail(MOCK_EMAIL);
        verifyNoInteractions(passwordEncoder, jwtService);
    }

    /**
     * Tests the scenario where the provided password does not match the stored encoded password.
     * It verifies that the controller returns an HTTP 401 Unauthorized status code with an "Invalid credentials" message.
     */
    @Test
    void login_InvalidPassword_ReturnsUnauthorized() {
        when(adminRepository.findByEmail(MOCK_EMAIL)).thenReturn(Optional.of(mockAdmin));
        when(passwordEncoder.matches(MOCK_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        ResponseEntity<String> response = adminAuthController.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());

        verify(adminRepository).findByEmail(MOCK_EMAIL);
        verify(passwordEncoder).matches(MOCK_PASSWORD, ENCODED_PASSWORD);
        verifyNoMoreInteractions(jwtService);
    }
}