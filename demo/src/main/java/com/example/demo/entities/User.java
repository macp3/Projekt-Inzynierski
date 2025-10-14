package com.example.demo.entities;

import java.time.LocalDate;

import org.jetbrains.annotations.NotNull;

import com.example.demo.entities.enums.DietTypes;
import com.example.demo.entities.enums.Status;

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
    @Column(unique = true)
    private String login;
    @NotNull
    private String password;

    @Column(name = "profile_picture")
    @Nullable
    private byte[] profilePicture;
    @Column(name = "premium_expiration")
    @Nullable
    private LocalDate premiumExpiration;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "preffered_diet")
    @Enumerated(EnumType.STRING)
    @Nullable
    private DietTypes prefferedDiet;
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
    public String getLogin() {
        return login;
    }

    public void setLogin(@NotNull String login) {
        this.login = login;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    @Nullable
    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(@Nullable byte[] profilePicture) {
        this.profilePicture = profilePicture;
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

    @Nullable
    public DietTypes getPrefferedDiet() {
        return prefferedDiet;
    }

    public void setPrefferedDiet(@Nullable DietTypes prefferedDiet) {
        this.prefferedDiet = prefferedDiet;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

}
