package com.example.demo.entities;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "user_trainings")
public class UserTraining
{
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @NotNull
    //czy tutaj kluczem glownym nie powinno byc po prostu pole "id" zamiast "user_id"?
    //bo tutaj klucz glowny ma auto inkrementacje i w momencie gdy user sobie przypisze (zmieni obecny na) kolejny trening to zinkrementuje nam sie user_id a nie cos inego a tak nie powinno byc bo w tej tabeli w tym momencie mozemy miec user_id równy 5 a faktycznie zarejestrowanych tylko dwoch uzytkownikow xd
    //help
    private int userId;
    @Column(name = "training_id")
    @NotNull
    private int trainingId;
    @NotNull
    private LocalDate timestamp;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(int trainingId) {
        this.trainingId = trainingId;
    }

    @NotNull
    public LocalDate getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(@NotNull LocalDate timestamp) {
        this.timestamp = timestamp;
    }
}
