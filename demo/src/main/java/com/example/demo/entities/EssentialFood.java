package com.example.demo.entities;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = "essential_food")
public class EssentialFood
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private int id;
    @NotNull
    private String name;
    @NotNull
    @Column(name = "author_id")
    private int authorId;
    @NotNull
    private String description;
    @NotNull
    private float calories;
    @NotNull
    private float protein;
    @NotNull
    private float fat;
    @NotNull
    private float carbohydrates;
    @NotNull
    @Column(name = "default_weight")
    private float defaultWeight;
}
