package study.snacktrack.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import study.snacktrack.dto.NotificationRequest;
import study.snacktrack.entities.Admin;
import study.snacktrack.entities.enums.Recipients;
import study.snacktrack.repositories.NotificationRepository;
import org.springframework.stereotype.Service;

import study.snacktrack.entities.Notification;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public Notification createNotification(NotificationRequest request, Admin author) {

        if (author == null) {
            throw new IllegalArgumentException("Author cant be null");
        }

        if (request.getName() == null || request.getName().isBlank() || request.getName() == "") {
            throw new IllegalArgumentException("Name cant be null");
        }

        if (request.getDescription() == null || request.getDescription().isBlank() || request.getDescription() == "") {
            throw new IllegalArgumentException("Description cant be null");
        }

        if (request.getRecipients() == null
                || (request.getRecipients() != Recipients.all && request.getRecipients() != Recipients.non_premium
                        && request.getRecipients() != Recipients.premium)) {
            throw new IllegalArgumentException("Wrong value of recipients");
        }

        if (request.getSendingTime() == null) {
            throw new IllegalArgumentException("Sending time cant be null");
        }

        Notification notification = new Notification();
        notification.setAuthor(author);
        notification.setName(request.getName());
        notification.setDescription(request.getDescription());
        notification.setRecipients(request.getRecipients());
        notification.setSendingTime(request.getSendingTime());

        return repository.save(notification);
    }

    public List<Notification> getAllNotifications() {
        return repository.findAll();
    }

    public List<Notification> getNotificationsByRecipients(Recipients recipients) {
        if (recipients == null
                || (recipients != Recipients.all && recipients != Recipients.non_premium
                        && recipients != Recipients.premium)) {
            throw new IllegalArgumentException("Wrong value of recipients");
        }
        return repository.findByRecipients(recipients);
    }

    public List<Notification> getNotificationsForUser(boolean isPremium) {
        List<Recipients> targets = new ArrayList<>();

        if (isPremium) {
            targets.add(Recipients.premium);
        } else {
            targets.add(Recipients.non_premium);
        }

        targets.add(Recipients.all);

        return repository.findByRecipientsIn(targets);
    }

    public List<Notification> getNotificationsByDate(LocalDate date) {
        return repository.findBySendingTime(date);
    }
}
