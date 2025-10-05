package com.example.demo.entities;

import com.example.demo.entities.enums.Sex;
import jakarta.persistence.*;

@Entity
@Table(name = "body_parameters")
public class BodyParameters
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;
    @Enumerated(EnumType.STRING)
    private Sex sex;
    private float height;
    private float weight;
    private int age;
    @Column(name = "sport_level")
    private int sportLevel;
    @Column(name = "goal_weight")
    private float goalWeight;
    @Column(name = "calorie_limit")
    private float calorieLimit;
    @Column(name = "protein_limit")
    private float proteinLimit;
    @Column(name = "fat_limit")
    private float fatLimit;
    @Column(name = "carbohydrates_limit")
    private float carbohydratesLimit;
}
