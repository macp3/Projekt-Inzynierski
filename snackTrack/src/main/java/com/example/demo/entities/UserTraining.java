package com.example.demo.entities;

import java.time.LocalDate;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = "user_trainings")
@IdClass(UserTrainingId.class)
public class UserTraining {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @NotNull
    private int userId;
    @Id
    @Column(name = "training_id")
    @NotNull
    private int trainingId;
    @NotNull
    private LocalDate timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_id", insertable = false, updatable = false)
    private TrainingInfo trainingInfo;


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
