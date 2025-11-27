package study.snacktrack.serviceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import study.snacktrack.services.JwtService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the JwtService, covering the core functionality of token generation, email extraction, and account type extraction.
 * This class ensures that the generated JSON Web Tokens are valid and contain the expected user information and claims.
 */
class JwtServiceTest {

    private JwtService jwtService;

    /**
     * Initializes a new instance of the {@code JwtService} before each test.
     * This provides a fresh, isolated environment for testing token operations.
     */
    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    /**
     * Tests the complete token lifecycle: generation and subsequent extraction of claims.
     * It verifies that the generated token is not null and that the extracted email and account type match the input values.
     */
    @Test
    void generateToken_shouldContainCorrectEmailAndAccountType() {
        String email = "test@example.com";
        String accountType = "ADMIN";

        String token = jwtService.generateToken(email, accountType);

        assertNotNull(token);

        String extractedEmail = jwtService.extractEmail(token);
        String extractedType = jwtService.extractAccountType(token);

        assertEquals(email, extractedEmail);
        assertEquals(accountType, extractedType);
    }

    /**
     * Tests the extraction of the email address (subject) from a generated token.
     * It ensures the {@code extractEmail} method correctly parses the token's payload to retrieve the subject claim.
     */
    @Test
    void extractEmail_shouldReturnCorrectSubject() {
        String token = jwtService.generateToken("user@example.com", "USER");

        String email = jwtService.extractEmail(token);

        assertEquals("user@example.com", email);
    }

    /**
     * Tests the extraction of the custom account type claim from a generated token.
     * It verifies that the {@code extractAccountType} method successfully retrieves the specific claim value defined during token creation.
     */
    @Test
    void extractAccountType_shouldReturnCorrectClaim() {
        String token = jwtService.generateToken("user@example.com", "USER");

        String type = jwtService.extractAccountType(token);

        assertEquals("USER", type);
    }
}