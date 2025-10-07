package com.example.demo.entities;

import com.example.demo.entities.enums.Recipients;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "notifications")
public class Notification
{
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    @Column(name = "author_id")
    private int authorId;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Recipients recipients;
    @NotNull
    @Column(name = "sending_time")
    private LocalDate sendingTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    @NotNull
    public Recipients getRecipients() {
        return recipients;
    }

    public void setRecipients(@NotNull Recipients recipients) {
        this.recipients = recipients;
    }

    @NotNull
    public LocalDate getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(@NotNull LocalDate sendingTime) {
        this.sendingTime = sendingTime;
    }
}
