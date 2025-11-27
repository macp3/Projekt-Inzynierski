package study.snacktrack.services;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for sending emails using JavaMailSender.
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${EMAIL_API_KEY}")
    private String apiKey;

    private String senderEmail = "snacktrack.notification@gmail.com";

    private String senderName = "SnackTrack";

    @Async
    public void sendEmail(String to, String subject, String content) {
        String url = "https://api.brevo.com/v3/smtp/email";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);
            headers.set("Accept", "application/json");

            Map<String, Object> body = new HashMap<>();

            Map<String, String> sender = new HashMap<>();
            sender.put("name", senderName);
            sender.put("email", senderEmail);
            body.put("sender", sender);

            Map<String, String> recipient = new HashMap<>();
            recipient.put("email", to);
            body.put("to", List.of(recipient));

            body.put("subject", subject);
            body.put("textContent", content);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("E-mail wysłany pomyślnie przez API do: " + to);
            } else {
                System.err.println("E-mail nie wyszedł. Status: " + response.getStatusCode());
                System.err.println("Odpowiedź: " + response.getBody());
            }

        } catch (Exception e) {
            System.err.println("BŁĄD API BREVO: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
