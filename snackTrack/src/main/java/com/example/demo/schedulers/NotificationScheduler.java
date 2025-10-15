package com.example.demo.schedulers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.entities.Notification;
import com.example.demo.entities.User;
import com.example.demo.services.NotificationService;
import com.example.demo.services.PushNotificationService;
import com.example.demo.services.UserService;

@Component
public class NotificationScheduler {

    private final NotificationService notificationService;
    private final UserService userService;
    private final PushNotificationService pushService;

    public NotificationScheduler(NotificationService notificationService, UserService userService, PushNotificationService pushService) {
        this.notificationService = notificationService;
        this.userService = userService;
        this.pushService = pushService;
    }

    @Scheduled(cron = "0 0 12 * * *")
    public void sendScheduledNotifications() {
        LocalDate today = LocalDate.now();
        List<Notification> todaysNotifications = notificationService.getNotificationsByDate(today);

        if (todaysNotifications.isEmpty()) {
            return;
        }

        for (Notification n : todaysNotifications) {
            List<User> recipients = userService.getUsersByRecipientType(n.getRecipients().name().toLowerCase());

            for (User u : recipients) {
                List<String> tokens = userService.getDeviceTokens(u.getId());
                for (String token : tokens) {
                    pushService.sendPush(token, n.getName(), n.getDescription());
                }
            }
        }
    }
}
