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
import org.springframework.test.web.servlet.MvcResult;
import study.snacktrack.entities.User;
import study.snacktrack.entities.VerificationToken;
import study.snacktrack.repositories.UserRepository;
import study.snacktrack.repositories.VerificationTokenRepository;

/**
 * Testuje pełen cykl uwierzytelnienia: Rejestracja -> Aktywacja -> Logowanie.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AuthIntegrationTest {

    @Autowired
    private VerificationTokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;

    // Przykładowe dane w formacie JSON
    private static final String REGISTER_REQUEST =
            "{"
                    + "\"email\":\"test@example.com\", "
                    + "\"password\":\"Haslo123!\","
                    + "\"name\":\"Jan\","
                    + "\"surname\":\"Kowalski\""
                    + "}";
    private static final String LOGIN_REQUEST =
            "{\"email\":\"test@example.com\", \"password\":\"Haslo123!\"}";


    @Test
    void testFullRegistrationAndLoginFlow() throws Exception {

        // KROK 1: Rejestracja (status 200 OK)
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_REQUEST))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User registered successfully")));


        // 1.5 POBRANIE TOKENA Z BAZY (Logika, którą już wdrożyłeś)
        User user = userRepository.findByEmail("test@example.com")
                .orElseThrow(() -> new AssertionError("User not found after registration"));

        // Ta metoda (findByUserId) musi istnieć w VerificationTokenRepository!
        VerificationToken verificationToken = tokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AssertionError("Verification token not found for user"));

        String tokenValue = verificationToken.getToken();


        // KROK 2: Próba logowania bez aktywacji (Status 403 Forbidden)
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_REQUEST))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Account not activated"));


        // KROK 3: Aktywacja konta - UŻYJ PRAWIDŁOWEGO ENDPOINTU: /auth/activate
        mockMvc.perform(get("/auth/activate") // <<< POPRAWIONA ŚCIEŻKA
                        .param("token", tokenValue)) // Najbezpieczniejszy sposób przekazywania parametru
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Account activated successfully!")));


        // KROK 4: Logowanie po aktywacji (powinno się udać, Status 200 OK)
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.showSurvey").value(true));
    }
}