package study.snacktrack.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import jakarta.annotation.PostConstruct;

/**
 * Configures and initializes the Firebase Admin SDK.
 */
@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_CONFIG_JSON}")
    private String firebaseConfigJson;

    /**
     * Initializes the FirebaseApp using the service account key.
     * Only runs if the app is not already initialized.
     *
     * @throws IOException If the service account file cannot be read.
     */
    @PostConstruct
    public void initializeFirebase() throws IOException {
        try {
            if (firebaseConfigJson == null || firebaseConfigJson.isBlank()) {
                return;
            }

            InputStream serviceAccount = new ByteArrayInputStream(firebaseConfigJson.getBytes(StandardCharsets.UTF_8));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
