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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getFat() {
        return fat;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    public float getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(float carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public float getDefaultWeight() {
        return defaultWeight;
    }

    public void setDefaultWeight(float defaultWeight) {
        this.defaultWeight = defaultWeight;
    }
}
