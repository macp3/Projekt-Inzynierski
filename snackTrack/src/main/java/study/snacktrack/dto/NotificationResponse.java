package study.snacktrack.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) used for carrying the details of a scheduled notification back to the client.
 * This structure includes the unique identifier, content, and the specific date the notification is set to be sent.
 */
public class NotificationResponse {

    private Integer id;
    private String name;
    private String description;
    private LocalDate sendingTime;

    /**
     * Constructs a new NotificationResponse object with the complete details of a scheduled notification.
     * This constructor is primarily used by the service layer to return notification data for display or management purposes.
     *
     * @param id          The unique ID of the notification.
     * @param name        The title or brief name of the notification.
     * @param description The full content or body of the notification.
     * @param sendingTime The date the notification is scheduled to be sent.
     */
    public NotificationResponse(Integer id, String name, String description, LocalDate sendingTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sendingTime = sendingTime;
    }

    /**
     * Getters and setters for the DTO fields.
     * These methods allow external access and modification of the notification's detailed properties.
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
}