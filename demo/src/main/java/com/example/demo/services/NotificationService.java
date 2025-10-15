package com.example.demo.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.NotificationRequest;
import com.example.demo.entities.Admin;
import com.example.demo.entities.Notification;
import com.example.demo.entities.enums.Recipients;
import com.example.demo.repositories.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public Notification createNotification(NotificationRequest request, Admin author) {
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
