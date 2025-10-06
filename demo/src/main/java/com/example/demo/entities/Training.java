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
}
