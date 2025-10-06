package com.example.demo.entities;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = "reported_meals")
public class ReportedMeal
{
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    @Column(name = "reporting_id")
    private int reportingId;
    @NotNull
    @Column(name = "meal_id")
    private int mealId;
    @NotNull
    private String content;
}
