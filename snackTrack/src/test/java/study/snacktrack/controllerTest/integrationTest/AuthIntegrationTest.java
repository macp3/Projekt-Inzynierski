package study.snacktrack.controllerTest.integrationTest;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import study.snacktrack.entities.User;
import study.snacktrack.entities.VerificationToken;
import study.snacktrack.repositories.UserRepository;
import study.snacktrack.repositories.VerificationTokenRepository;

/**
 * Tests the complete authentication cycle including registration, activation, and login flow.
 * This class uses MockMvc to simulate HTTP requests against the running application context, ensuring all layers function correctly together.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class AuthIntegrationTest {

    @Autowired
    private VerificationTokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;

    /**
     * Example data in JSON format for registration and login.
     */
    private static final String REGISTER_REQUEST =
            "{"
                    + "\"email\":\"test@example.com\", "
                    + "\"password\":\"Haslo123!\","
                    + "\"name\":\"Jan\","
                    + "\"surname\":\"Kowalski\""
                    + "}";
    private static final String LOGIN_REQUEST =
            "{\"email\":\"test@example.com\", \"password\":\"Haslo123!\"}";


    /**
     * Executes the full scenario: successful registration, failed login before activation, successful activation, and final successful login.
     * This integration test validates the crucial authentication process across multiple application endpoints and database interactions.
     */
    @Test
    void testFullRegistrationAndLoginFlow() throws Exception {

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_REQUEST))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User registered successfully")));


        User user = userRepository.findByEmail("test@example.com")
                .orElseThrow(() -> new AssertionError("User not found after registration"));

        VerificationToken verificationToken = tokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AssertionError("Verification token not found for user"));

        String tokenValue = verificationToken.getToken();


        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_REQUEST))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Account not activated"));


        mockMvc.perform(get("/auth/activate")
                        .param("token", tokenValue))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Account activated successfully!")));


        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.showSurvey").value(true));
    }
}