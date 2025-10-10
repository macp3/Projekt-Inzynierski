package com.example.demo.entities;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = "meal_diet_type")
public class MealDietType
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private int id;
    @Column(name = "meal_id")
    private int mealId;
    @Column(name = "diet_type_id")
    private int dietTypeId;
}
