package com.example.demo.entities;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "exercises")
public class Exercise
{
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private String type;
    @NotNull
    private int difficulty;
    @NotNull
    @Column(name = "number_of_sets")
    private int numberOfSets;
    @NotNull
    private int intensivity;
}
