package com.example.demo.entities;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

@Entity
@Table(name="badges")
public class Badge
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private int id;
    @Column(name = "user_id")
    @NotNull
    private int userId;
    @NotNull
    private String badge;
    @Column(name = "badge_logo")
    @NotNull
    private BufferedImage badgeLogo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @NotNull
    public String getBadge() {
        return badge;
    }

    public void setBadge(@NotNull String badge) {
        this.badge = badge;
    }

    @NotNull
    public BufferedImage getBadgeLogo() {
        return badgeLogo;
    }

    public void setBadgeLogo(@NotNull BufferedImage badgeLogo) {
        this.badgeLogo = badgeLogo;
    }
}
