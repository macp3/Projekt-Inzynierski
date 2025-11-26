package study.snacktrack.serviceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import study.snacktrack.services.JwtService;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

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

    @Test
    void extractEmail_shouldReturnCorrectSubject() {
        String token = jwtService.generateToken("user@example.com", "USER");

        String email = jwtService.extractEmail(token);

        assertEquals("user@example.com", email);
    }

    @Test
    void extractAccountType_shouldReturnCorrectClaim() {
        String token = jwtService.generateToken("user@example.com", "USER");

        String type = jwtService.extractAccountType(token);

        assertEquals("USER", type);
    }
}