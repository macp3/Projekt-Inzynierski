package com.example.demo.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Entity
@Table(name = "registered_alimentation")
public class RegisteredAlimentation
{
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    @Column(name = "user_id")
    private int userId;
    @NotNull
    @Column(name = "essential_id")
    private int essentialId;
    @Column(name = "meal_api_id")
    @Nullable
    private int mealApiId;
    @Column(name = "meal_id")
    @Nullable
    private int mealId;
    @NotNull
    private Date timestamp;
    @NotNull
    private float weight;
    @NotNull
    private int amount;
}
