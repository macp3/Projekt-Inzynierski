package com.example.demo.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "registered_alimentation")
public class RegisteredAlimentation
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "user_id")
    private int userId;
    @Column(name = "essential_id")
    private int essentialId;
    @Column(name = "meal_api_id")
    @Nullable
    private int mealApiId;
    @Column(name = "meal_id")
    @Nullable
    private int mealId;
    private Date timestamp;
    private float weight;
    private int amount;
}
