package com.example.demo.entities;

import com.example.demo.entities.enums.Sex;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "body_parameters")
public class BodyParameters
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @NotNull
    private int userId;
    @Enumerated(EnumType.STRING)
    @NotNull
    private Sex sex;
    @NotNull
    private float height;
    @NotNull
    private float weight;
    @NotNull
    private int age;
    @Column(name = "sport_level")
    @NotNull
    private int sportLevel;
    @Column(name = "goal_weight")
    @NotNull
    private float goalWeight;
    @Column(name = "calorie_limit")
    @NotNull
    private float calorieLimit;
    @Column(name = "protein_limit")
    @NotNull
    private float proteinLimit;
    @Column(name = "fat_limit")
    @NotNull
    private float fatLimit;
    @Column(name = "carbohydrates_limit")
    @NotNull
    private float carbohydratesLimit;
}
