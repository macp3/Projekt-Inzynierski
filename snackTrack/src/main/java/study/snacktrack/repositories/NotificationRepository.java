package study.snacktrack.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import study.snacktrack.entities.Notification;
import study.snacktrack.entities.enums.Recipients;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByRecipients(Recipients recipients);

    List<Notification> findByRecipientsIn(List<Recipients> recipients);

    List<Notification> findBySendingTime(LocalDate date);
}
