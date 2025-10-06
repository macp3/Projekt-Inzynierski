package com.example.demo.entities;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = "trainings")
public class Training
{
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    @Column(name = "training_id")
    private int trainingId;
    @NotNull
    @Column(name = "author_id")
    private int authorId;
    @NotNull
    @Column(name = "exercise_id")
    private int exerciseId;
    @NotNull
    @Column(name = "day_of_exercise")
    private int dayOfExercise;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(int trainingId) {
        this.trainingId = trainingId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public int getDayOfExercise() {
        return dayOfExercise;
    }

    public void setDayOfExercise(int dayOfExercise) {
        this.dayOfExercise = dayOfExercise;
    }
}