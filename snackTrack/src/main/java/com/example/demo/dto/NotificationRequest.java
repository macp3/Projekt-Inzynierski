package com.example.demo.dto;

import java.time.LocalDate;

import com.example.demo.entities.enums.Recipients;

public class NotificationRequest {

    private String name;
    private String description;
    private Recipients recipients;
    private LocalDate sendingTime;

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
