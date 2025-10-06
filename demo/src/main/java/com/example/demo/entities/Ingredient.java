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


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public int getEssentialId() {
        return essentialId;
    }

    public void setEssentialId(int essentialId) {
        this.essentialId = essentialId;
    }

    public int getEssentialApiId() {
        return essentialApiId;
    }

    public void setEssentialApiId(int essentialApiId) {
        this.essentialApiId = essentialApiId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
