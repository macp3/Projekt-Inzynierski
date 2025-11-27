package study.snacktrack.services;

import org.springframework.stereotype.Service;
import study.snacktrack.dto.NotificationRequest;
import study.snacktrack.dto.NotificationResponse;
import study.snacktrack.entities.Admin;
import study.snacktrack.entities.Notification;
import study.snacktrack.entities.User;
import study.snacktrack.entities.enums.Recipients;
import study.snacktrack.repositories.NotificationRepository;
import study.snacktrack.repositories.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for managing notifications.
 * Provides functionality for creating, retrieving, and filtering notifications.
 */
@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final UserRepository userRepository;

    /**
     * Constructs NotificationService with required repositories.
     *
     * @param repository notification repository
     * @param userRepository user repository
     */
    public NotificationService(NotificationRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new notification authored by an admin.
     *
     * @param request notification request data
     * @param author admin author
     * @return created notification
     */
    public Notification createNotification(NotificationRequest request, Admin author) {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Name cannot be null");
        }

        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description cannot be null");
        }

        if (request.getRecipients() == null
                || (request.getRecipients() != Recipients.all && request.getRecipients() != Recipients.non_premium
                && request.getRecipients() != Recipients.premium)) {
            throw new IllegalArgumentException("Wrong value of recipients");
        }

        if (request.getSendingTime() == null) {
            throw new IllegalArgumentException("Sending time cannot be null");
        }

        Notification notification = new Notification();
        notification.setAuthor(author);
        notification.setName(request.getName());
        notification.setDescription(request.getDescription());
        notification.setRecipients(request.getRecipients());
        notification.setSendingTime(request.getSendingTime());

        return repository.save(notification);
    }

    /**
     * Retrieves all notifications.
     *
     * @return list of notifications
     */
    public List<Notification> getAllNotifications() {
        return repository.findAll();
    }

    /**
     * Retrieves all notifications with details mapped to response DTOs.
     *
     * @return list of notification responses
     */
    public List<NotificationResponse> getAllNotificationsDetails() {
        List<Notification> notificationList = repository.findAll();
        List<NotificationResponse> notificationDetails = new ArrayList<>();
        for (Notification n : notificationList) {
            NotificationResponse response = new NotificationResponse(n.getId(), n.getName(), n.getDescription(),
                    n.getSendingTime());
            notificationDetails.add(response);
        }
        return notificationDetails;
    }

    /**
     * Retrieves notifications filtered by recipients.
     *
     * @param recipients target recipients
     * @return list of notifications
     */
    public List<Notification> getNotificationsByRecipients(Recipients recipients) {
        if (recipients == null
                || (recipients != Recipients.all && recipients != Recipients.non_premium
                && recipients != Recipients.premium)) {
            throw new IllegalArgumentException("Wrong value of recipients");
        }
        return repository.findByRecipients(recipients);
    }

    /**
     * Retrieves notifications filtered by recipients, mapped to response DTOs.
     *
     * @param recipients target recipients
     * @return list of notification responses
     */
    public List<NotificationResponse> getNotificationsByRecipientsWithoutDetails(Recipients recipients) {
        if (recipients == null
                || (recipients != Recipients.all && recipients != Recipients.non_premium
                && recipients != Recipients.premium)) {
            throw new IllegalArgumentException("Wrong value of recipients");
        }

        List<Notification> notificationList = repository.findAll();
        List<NotificationResponse> notificationDetails = new ArrayList<>();
        for (Notification n : notificationList) {
            if (n.getRecipients().equals(recipients)) {
                NotificationResponse response = new NotificationResponse(n.getId(), n.getName(), n.getDescription(),
                        n.getSendingTime());
                notificationDetails.add(response);
            }
        }
        return notificationDetails;
    }

    /**
     * Retrieves notifications for a specific user based on premium status.
     * Only notifications scheduled for today are returned.
     *
     * @param userId user identifier
     * @return list of notification responses
     */
    public List<NotificationResponse> getNotificationsByUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Notification> notifications = new ArrayList<>();

        if (user.getPremiumExpiration() != null) {
            notifications = repository.findByRecipientsIn(List.of(Recipients.all, Recipients.premium));
        } else {
            notifications = repository.findByRecipientsIn(List.of(Recipients.all, Recipients.non_premium));
        }

        List<NotificationResponse> response = new ArrayList<>();
        for (var notif : notifications) {
            if (notif.getSendingTime().isEqual(LocalDate.now())) {
                response.add(new NotificationResponse(notif.getId(), notif.getName(), notif.getDescription(),
                        notif.getSendingTime()));
            }
        }

        return response;
    }

    /**
     * Retrieves notifications scheduled for a specific date.
     *
     * @param date target date
     * @return list of notifications
     */
    public List<Notification> getNotificationsByDate(LocalDate date) {
        return repository.findBySendingTime(date);
    }
}
