package com.example.demo.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "reported_meals")
public class ReportedMeal
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "reporting_id")
    private int reportingId;
    @Column(name = "meal_id")
    private int mealId;
    private String content;
}
