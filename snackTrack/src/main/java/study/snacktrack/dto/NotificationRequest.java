package study.snacktrack.dto;

import java.time.LocalDate;

import study.snacktrack.entities.enums.Recipients;

/**
 * Data Transfer Object (DTO) used for carrying information required to create a new scheduled notification.
 * This structure includes the notification's content, the target audience, and the desired sending date.
 */
public class NotificationRequest {

    private String name;
    private String description;
    private Recipients recipients;
    private LocalDate sendingTime;

    /**
     * Getters and setters for the DTO fields.
     * These methods provide standard access and modification capabilities for the notification's properties.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Recipients getRecipients() {
        return recipients;
    }

    public void setRecipients(Recipients recipients) {
        this.recipients = recipients;
    }

    public LocalDate getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(LocalDate sendingTime) {
        this.sendingTime = sendingTime;
    }
}