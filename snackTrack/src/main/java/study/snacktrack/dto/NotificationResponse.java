package study.snacktrack.dto;

import java.time.LocalDate;

public class NotificationResponse {

    private Integer id;
    private String name;
    private String description;
    private LocalDate sendingTime;

    public NotificationResponse(Integer id, String name, String description, LocalDate sendingTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sendingTime = sendingTime;
    }

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

    public LocalDate getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(LocalDate sendingTime) {
        this.sendingTime = sendingTime;
    }
}
