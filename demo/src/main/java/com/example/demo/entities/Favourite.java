package com.example.demo.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = "favourite")
public class Favourite
{
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    @Column(name = "user_id")
    private int userId;
    @Column(name = "meal_id")
    @Nullable
    private int mealId;
    @Column(name = "meal_api_id")
    @Nullable
    private int mealApiId;
}
