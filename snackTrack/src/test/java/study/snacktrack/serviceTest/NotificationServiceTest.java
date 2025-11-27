package study.snacktrack.serviceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import study.snacktrack.entities.enums.Recipients;
import study.snacktrack.repositories.*;
import study.snacktrack.entities.*;
import study.snacktrack.services.*;
import study.snacktrack.dto.NotificationResponse;
import study.snacktrack.dto.NotificationRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the NotificationService, covering the business logic for creating, fetching, and filtering notifications.
 * This class ensures that the service correctly interacts with the repositories and applies recipient filtering based on user status.
 */
class NotificationServiceTest {

    private NotificationRepository notificationRepository;
    private UserRepository userRepository;
    private NotificationService notificationService;

    /**
     * Sets up mock repositories and initializes the NotificationService instance before each test.
     * This isolates the service logic, allowing for verification of correct repository method calls.
     */
    @BeforeEach
    void setUp() {
        notificationRepository = mock(NotificationRepository.class);
        userRepository = mock(UserRepository.class);
        notificationService = new NotificationService(notificationRepository, userRepository);
    }

    /**
     * Tests the successful creation and saving of a new notification by an administrator.
     * It verifies that the service saves the entity and returns the created notification object.
     */
    @Test
    void createNotification_shouldSaveAndReturnNotification() {
        Admin admin = new Admin();
        NotificationRequest request = new NotificationRequest();
        request.setName("Test");
        request.setDescription("Desc");
        request.setRecipients(Recipients.all);
        request.setSendingTime(LocalDate.now());

        Notification saved = new Notification();
        saved.setId(1);
        saved.setAuthor(admin);
        saved.setName("Test");
        saved.setDescription("Desc");
        saved.setRecipients(Recipients.all);
        saved.setSendingTime(LocalDate.now());

        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

        Notification result = notificationService.createNotification(request, admin);

        assertEquals("Test", result.getName());
        assertEquals(Recipients.all, result.getRecipients());
        verify(notificationRepository).save(any(Notification.class));
    }

    /**
     * Tests that notification creation fails if the author (Admin) object is null.
     * It verifies that the service throws an {@code IllegalArgumentException} to enforce the author requirement.
     */
    @Test
    void createNotification_shouldThrowWhenAuthorIsNull() {
        NotificationRequest request = new NotificationRequest();
        request.setName("Test");
        request.setDescription("Desc");
        request.setRecipients(Recipients.all);
        request.setSendingTime(LocalDate.now());

        assertThrows(IllegalArgumentException.class,
                () -> notificationService.createNotification(request, null));
    }

    /**
     * Tests the retrieval of all notifications present in the database.
     * It verifies that the service fetches the entire list of {@code Notification} entities.
     */
    @Test
    void getAllNotifications_shouldReturnList() {
        Notification n = new Notification();
        n.setId(1);
        n.setName("N1");
        n.setDescription("D1");
        n.setRecipients(Recipients.all);
        n.setSendingTime(LocalDate.now());

        when(notificationRepository.findAll()).thenReturn(List.of(n));

        List<Notification> result = notificationService.getAllNotifications();

        assertEquals(1, result.size());
        assertEquals("N1", result.get(0).getName());
    }

    /**
     * Tests the retrieval of all notifications mapped to response DTOs.
     * It verifies that the service converts the fetched entities into {@code NotificationResponse} objects.
     */
    @Test
    void getAllNotificationsDetails_shouldReturnResponses() {
        Notification n = new Notification();
        n.setId(1);
        n.setName("N1");
        n.setDescription("D1");
        n.setRecipients(Recipients.all);
        n.setSendingTime(LocalDate.now());

        when(notificationRepository.findAll()).thenReturn(List.of(n));

        List<NotificationResponse> result = notificationService.getAllNotificationsDetails();

        assertEquals(1, result.size());
        assertEquals("N1", result.get(0).getName());
    }

    /**
     * Tests the retrieval of notifications filtered by a specific recipient type (e.g., premium users).
     * It verifies that the service delegates the filtering to the repository method.
     */
    @Test
    void getNotificationsByRecipients_shouldReturnList() {
        Notification n = new Notification();
        n.setId(2);
        n.setName("PremiumNotif");
        n.setDescription("D2");
        n.setRecipients(Recipients.premium);
        n.setSendingTime(LocalDate.now());

        when(notificationRepository.findByRecipients(Recipients.premium)).thenReturn(List.of(n));

        List<Notification> result = notificationService.getNotificationsByRecipients(Recipients.premium);

        assertEquals(1, result.size());
        assertEquals(Recipients.premium, result.get(0).getRecipients());
    }

    /**
     * Tests the retrieval of notifications relevant to a specific user, considering their premium status.
     * It verifies that the service correctly determines the necessary recipient types (all and premium) and fetches the filtered list.
     */
    @Test
    void getNotificationsByUser_shouldReturnPremiumNotifications() {
        User user = new User();
        user.setId(10);
        user.setPremiumExpiration(LocalDate.now().plusDays(1));

        Notification n = new Notification();
        n.setId(3);
        n.setName("PremiumNotif");
        n.setDescription("D3");
        n.setRecipients(Recipients.premium);
        n.setSendingTime(LocalDate.now());

        when(userRepository.findById(10)).thenReturn(Optional.of(user));
        when(notificationRepository.findByRecipientsIn(List.of(Recipients.all, Recipients.premium)))
                .thenReturn(List.of(n));

        List<NotificationResponse> result = notificationService.getNotificationsByUser(10);

        assertEquals(1, result.size());
        assertEquals("PremiumNotif", result.get(0).getName());
    }

    /**
     * Tests the retrieval of notifications scheduled to be sent on a specific date.
     * It verifies that the service delegates the request to the repository's date-based search method.
     */
    @Test
    void getNotificationsByDate_shouldReturnList() {
        Notification n = new Notification();
        n.setId(4);
        n.setName("DailyNotif");
        n.setDescription("D4");
        n.setRecipients(Recipients.all);
        n.setSendingTime(LocalDate.now());

        when(notificationRepository.findBySendingTime(LocalDate.now())).thenReturn(List.of(n));

        List<Notification> result = notificationService.getNotificationsByDate(LocalDate.now());

        assertEquals(1, result.size());
        assertEquals("DailyNotif", result.get(0).getName());
    }
}