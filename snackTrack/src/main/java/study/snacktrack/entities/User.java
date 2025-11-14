package study.snacktrack.entities;

import java.time.LocalDate;

import study.snacktrack.entities.enums.Status;
import org.jetbrains.annotations.NotNull;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    private String name;
    @NotNull
    private String surname;
    @NotNull
    @Column(unique = true)
    private String email;
    @NotNull
    private String password;

    @Column(name = "image_url")
    @Nullable
    private String imageUrl;
    @Column(name = "premium_expiration")
    @Nullable
    private LocalDate premiumExpiration;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;
    @NotNull
    private int streak;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public String getSurname() {
        return surname;
    }

    public void setSurname(@NotNull String surname) {
        this.surname = surname;
    }

    @NotNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    @Nullable
    public LocalDate getPremiumExpiration() {
        return premiumExpiration;
    }

    public void setPremiumExpiration(@Nullable LocalDate premiumExpiration) {
        this.premiumExpiration = premiumExpiration;
    }

    @NotNull
    public Status getStatus() {
        return status;
    }

    public void setStatus(@NotNull Status status) {
        this.status = status;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@Nullable String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
