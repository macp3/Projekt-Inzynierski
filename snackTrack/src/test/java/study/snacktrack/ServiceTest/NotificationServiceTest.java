package study.snacktrack.ServiceTest;

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

class NotificationServiceTest {

    private NotificationRepository notificationRepository;
    private UserRepository userRepository;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationRepository = mock(NotificationRepository.class);
        userRepository = mock(UserRepository.class);
        notificationService = new NotificationService(notificationRepository, userRepository);
    }

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