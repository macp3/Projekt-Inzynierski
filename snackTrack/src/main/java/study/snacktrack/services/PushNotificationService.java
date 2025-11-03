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

@Service
public class PushNotificationService {

    private final UserRepository userRepository;
    private final UserDeviceTokenRepository tokenRepository;

    public PushNotificationService(UserRepository userRepository, UserDeviceTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public void sendToGroup(String targetGroup, String title, String body) {
        if (targetGroup == null || targetGroup == null || title.isBlank() || title == null || body.isBlank()
                || body == null) {
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

        List<String> tokens = tokenRepository.findAll().stream()
                .filter(t -> userIds.contains(t.getUserId()))
                .map(UserDeviceToken::getDeviceToken)
                .filter(token -> token != null && !token.isBlank())
                .collect(Collectors.toList());

        System.out.println(tokens);

        if (tokens.isEmpty()) {
            return;
        }

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            FirebaseMessaging.getInstance().sendEachForMulticast(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send notifications: " + e.getMessage());
        }
    }

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
            System.err.println("❌ Error sending multicast push: " + e.getMessage());
        }
    }
}
