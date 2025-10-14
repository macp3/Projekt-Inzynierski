package com.example.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "trainings")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "training_id", nullable = false)
    private Integer trainingId;

    @Column(name = "author_id", nullable = false)
    private Integer authorId;

    @Column(name = "exercise_id", nullable = false)
    private Integer exerciseId;

    @Column(name = "day_of_exercise", nullable = false)
    private Integer dayOfExercise;

    // Gettery i settery
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(Integer trainingId) {
        this.trainingId = trainingId;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public Integer getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Integer exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Integer getDayOfExercise() {
        return dayOfExercise;
    }

    public void setDayOfExercise(Integer dayOfExercise) {
        this.dayOfExercise = dayOfExercise;
    }
}
