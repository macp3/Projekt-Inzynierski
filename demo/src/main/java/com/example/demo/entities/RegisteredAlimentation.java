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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEssentialId() {
        return essentialId;
    }

    public void setEssentialId(int essentialId) {
        this.essentialId = essentialId;
    }

    public int getMealApiId() {
        return mealApiId;
    }

    public void setMealApiId(int mealApiId) {
        this.mealApiId = mealApiId;
    }

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    @NotNull
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(@NotNull Date timestamp) {
        this.timestamp = timestamp;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
