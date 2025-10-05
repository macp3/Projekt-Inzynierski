package com.example.demo.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "essential_food")
public class EssentialFood
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @Column(name = "author_id")
    private int authorId;
    private String description;
    private float calories;
    private float protein;
    private float fat;
    private float carbohydrates;
    @Column(name = "default_weight")
    private float defaultWeight;
}
