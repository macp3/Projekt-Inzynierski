package com.example.demo.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

@Entity
@Table(name = "favourite")
public class Favourite
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "user_id")
    private int userId;
    @Column(name = "meal_id")
    @Nullable
    private int mealId;
    @Column(name = "meal_api_id")
    @Nullable
    private int mealApiId;
}
