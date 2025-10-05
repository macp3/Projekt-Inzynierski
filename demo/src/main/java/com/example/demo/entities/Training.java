package com.example.demo.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "trainings")
public class Training
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "training_id")
    private int trainingId;
    @Column(name = "author_id")
    private int authorId;
    @Column(name = "exercise_id")
    private int exerciseId;
    @Column(name = "day_of_exercise")
    private int dayOfExercise;
}
