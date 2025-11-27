package study.snacktrack.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import study.snacktrack.entities.Notification;
import study.snacktrack.entities.enums.Recipients;

/**
 * Repository interface for managing Notification entities.
 * This extends JpaRepository to handle standard persistence operations and custom methods for retrieving scheduled notifications.
 */
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    /**
     * Retrieves all notifications targeted at a specific recipient group defined by the Recipients enum.
     * This method allows fetching notifications intended for groups like 'premium' or 'all' users.
     *
     * @param recipients the target recipient type
     * @return a List of Notification entities for the specified recipient group
     */
    List<Notification> findByRecipients(Recipients recipients);

    /**
     * Finds notifications that are targeted at any of the recipient types provided in the list.
     * This is useful for retrieving a broader set of notifications intended for multiple specified groups.
     *
     * @param recipients a List of target recipient types
     * @return a List of Notification entities matching any of the specified recipient groups
     */
    List<Notification> findByRecipientsIn(List<Recipients> recipients);

    /**
     * Retrieves all notifications that are scheduled to be sent on a particular date.
     * This method is crucial for the scheduler to determine which notifications need to be processed on a given day.
     *
     * @param date the sending date to check
     * @return a List of Notification entities scheduled for the given date
     */
    List<Notification> findBySendingTime(LocalDate date);
}