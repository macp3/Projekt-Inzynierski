package study.snacktrack.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

/**
 * Service responsible for handling authentication with FatSecret API.
 * Manages access tokens and refreshes them when expired.
 */
@Service
public class FatSecretAuthService {

    @Value("${fatsecret.client-id}")
    private String clientId;

    @Value("${fatsecret.client-secret}")
    private String clientSecret;

    private String accessToken;
    private long tokenExpirationTime = 0;

    /**
     * Returns a valid access token.
     * Refreshes the token if it is missing or expired.
     *
     * @return FatSecret API access token
     */
    public synchronized String getAccessToken() {
        long now = System.currentTimeMillis();

        if (accessToken == null || now >= tokenExpirationTime) {
            refreshToken();
        }

        return accessToken;
    }

    /**
     * Requests a new access token from FatSecret API.
     * Updates the token and its expiration time.
     */
    private void refreshToken() {
        String url = "https://oauth.fatsecret.com/connect/token";

        RestTemplate restTemplate = new RestTemplate();

        String credentials = clientId + ":" + clientSecret;
        String base64Creds = Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + base64Creds);

        String body = "grant_type=client_credentials&scope=basic";

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            this.accessToken = (String) responseBody.get("access_token");
            Integer expiresIn = (Integer) responseBody.get("expires_in");
            this.tokenExpirationTime = System.currentTimeMillis() + (expiresIn - 60) * 1000L;
        } else {
            throw new RuntimeException("Failed to fetch FatSecret token: " + response.getStatusCode());
        }
    }
}
