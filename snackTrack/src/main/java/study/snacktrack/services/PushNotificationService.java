package study.snacktrack.services;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;
import study.snacktrack.entities.User;
import study.snacktrack.entities.UserDeviceToken;
import study.snacktrack.repositories.UserDeviceTokenRepository;
import study.snacktrack.repositories.UserRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsible for sending push notifications using Firebase Cloud Messaging.
 * Supports sending to specific user groups or multiple device tokens.
 */
@Service
public class PushNotificationService {

    private final UserRepository userRepository;
    private final UserDeviceTokenRepository tokenRepository;

    /**
     * Constructs PushNotificationService with required repositories.
     *
     * @param userRepository repository for users
     * @param tokenRepository repository for user device tokens
     */
    public PushNotificationService(UserRepository userRepository, UserDeviceTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    /**
     * Sends a push notification to a specific group of users.
     * Groups can be "premium", "non_premium", or "all".
     *
     * @param targetGroup target group name
     * @param title notification title
     * @param body notification body
     */
    public void sendToGroup(String targetGroup, String title, String body) {
        if (targetGroup == null || title == null || body == null || title.isBlank() || body.isBlank()) {
            throw new IllegalArgumentException("Wrong parameters");
        }
        List<User> users = userRepository.findAll();

        List<Integer> userIds = switch (targetGroup.toLowerCase()) {
            case "premium" -> users.stream()
                    .filter(u -> u.getPremiumExpiration() != null && u.getPremiumExpiration().isAfter(LocalDate.now()))
                    .map(User::getId)
                    .toList();
            case "non_premium" -> users.stream()
                    .filter(u -> u.getPremiumExpiration() == null || u.getPremiumExpiration().isBefore(LocalDate.now()))
                    .map(User::getId)
                    .toList();
            case "all" -> users.stream()
                    .map(User::getId)
                    .toList();
            default -> throw new IllegalArgumentException("Invalid target group: " + targetGroup);
        };

        Set<String> tokens = tokenRepository.findAll().stream()
                .filter(t -> userIds.contains(t.getUserId()))
                .map(UserDeviceToken::getDeviceToken)
                .filter(token -> token != null && !token.isBlank())
                .collect(Collectors.toSet());

        if (tokens.isEmpty()) {
            return;
        }

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(new ArrayList<>(tokens))
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData("title", title)
                .putData("body", body)
                .build();

        try {
            BatchResponse batchResponse = FirebaseMessaging.getInstance().sendEachForMulticast(message);

            List<SendResponse> responses = batchResponse.getResponses();
            List<String> tokenList = new ArrayList<>(tokens);

            for (int i = 0; i < responses.size(); i++) {
                SendResponse response = responses.get(i);
                if (!response.isSuccessful()) {
                    String failedToken = tokenList.get(i);
                    tokenRepository.deleteByDeviceToken(failedToken);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to send notifications: " + e.getMessage());
        }
    }

    /**
     * Sends a multicast push notification to a list of device tokens.
     *
     * @param deviceTokens list of device tokens
     * @param title notification title
     * @param body notification body
     */
    public void sendMulticastPush(List<String> deviceTokens, String title, String body) {
        if (deviceTokens == null || deviceTokens.isEmpty()) {
            return;
        }

        try {
            com.google.firebase.messaging.Notification notification = com.google.firebase.messaging.Notification
                    .builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            MulticastMessage message = MulticastMessage.builder()
                    .addAllTokens(deviceTokens)
                    .setNotification(notification)
                    .build();

            FirebaseMessaging.getInstance()
                    .sendEachForMulticast(message);

        } catch (Exception e) {
            System.err.println("Error sending multicast push: " + e.getMessage());
        }
    }
}
