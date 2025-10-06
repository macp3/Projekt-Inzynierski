package com.example.demo.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = "ingredients")
public class Ingredient
{
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "meal_id")
    @Nullable
    private int mealId;
    @Column(name = "essential_id")
    @Nullable
    private int essentialId;
    @Column(name = "essential_api_id")
    @Nullable
    private int essentialApiId;
    @NotNull
    private float amount;
}
