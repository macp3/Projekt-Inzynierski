package study.snacktrack.schedulers;

import java.time.LocalDate;
import java.util.List;

import study.snacktrack.services.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import study.snacktrack.entities.Notification;
import study.snacktrack.entities.User;
import study.snacktrack.services.NotificationService;
import study.snacktrack.services.PushNotificationService;

/**
 * A scheduled component responsible for executing time-based tasks, specifically sending push notifications.
 * It uses Spring's @Scheduled annotation to run notification delivery at a fixed time each day.
 */
@Component
public class NotificationScheduler {

    private final NotificationService notificationService;
    private final UserService userService;
    private final PushNotificationService pushService;

    /**
     * Constructs the NotificationScheduler with necessary service dependencies.
     */
    public NotificationScheduler(NotificationService notificationService, UserService userService,
                                 PushNotificationService pushService) {
        this.notificationService = notificationService;
        this.userService = userService;
        this.pushService = pushService;
    }

    /**
     * Executes daily to retrieve and send scheduled notifications to the appropriate user groups.
     * This method runs every day at 12:00 PM (noon) based on the cron expression "0 0 12 * * *".
     */
    @Scheduled(cron = "0 0 12 * * *")
    public void sendScheduledNotifications() {
        LocalDate today = LocalDate.now();
        List<Notification> todaysNotifications = notificationService.getNotificationsByDate(today);

        if (todaysNotifications.isEmpty()) {
            return;
        }

        for (Notification n : todaysNotifications) {
            List<User> recipients = userService.getUsersByRecipientType(n.getRecipients().name().toLowerCase());

            List<String> allTokens = recipients.stream()
                    .flatMap(user -> userService.getDeviceTokens(user.getId()).stream())
                    .distinct()
                    .toList();

            if (!allTokens.isEmpty()) {
                pushService.sendMulticastPush(allTokens, n.getName(), n.getDescription());
            }
        }
    }
}