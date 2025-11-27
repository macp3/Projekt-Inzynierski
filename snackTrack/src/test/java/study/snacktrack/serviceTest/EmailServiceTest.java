package study.snacktrack.serviceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import study.snacktrack.services.EmailService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the EmailService, ensuring the service correctly utilizes the JavaMailSender to send simple email messages.
 * This class verifies that the email content (recipient, subject, and text) is correctly mapped before being dispatched by the mock sender.
 */
class EmailServiceTest {

    private JavaMailSender mailSender;
    private EmailService emailService;

    /**
     * Sets up a mock instance of {@code JavaMailSender} and initializes the {@code EmailService} with it before each test.
     * This isolates the service logic, allowing us to verify the interactions with the mail sender component.
     */
    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        emailService = new EmailService(mailSender);
    }

    /**
     * Tests the {@code sendEmail} method to ensure it correctly constructs and sends an email message.
     * It uses an {@code ArgumentCaptor} to verify that the captured {@code SimpleMailMessage} contains the correct 'to', 'subject', and 'text' fields.
     */
    @Test
    void sendEmail_shouldSendMessageWithCorrectFields() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Hello, this is a test email.";

        emailService.sendEmail(to, subject, text);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage captured = captor.getValue();
        assertEquals(to, captured.getTo()[0]);
        assertEquals(subject, captured.getSubject());
        assertEquals(text, captured.getText());
    }
}