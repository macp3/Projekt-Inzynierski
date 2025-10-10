package com.example.demo.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = "ingredients")
public class Ingredient {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id")
    private Meal meal;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "essential_id")
    private EssentialFood essentialFood;
    @Column(name = "essential_api_id")
    @Nullable
    private Integer essentialApiId;
    @NotNull
    private float amount;
    @NotNull
    @Column(name = "default_unit")
    private String defaultUnit;

    public Ingredient() {

    }

    public Ingredient(Meal meal, EssentialFood essentialFood, @Nullable Integer essentialApiId, float amount, @NotNull String defaultUnit) {
        this.meal = meal;
        this.essentialFood = essentialFood;
        this.essentialApiId = essentialApiId;
        this.amount = amount;
        this.defaultUnit = defaultUnit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    public EssentialFood getEssentialFood() {
        return essentialFood;
    }

    public void setEssentialFood(EssentialFood essentialFood) {
        this.essentialFood = essentialFood;
    }

    @Nullable
    public Integer getEssentialApiId() {
        return essentialApiId;
    }

    public void setEssentialApiId(@Nullable Integer essentialApiId) {
        this.essentialApiId = essentialApiId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @NotNull
    public String getDefaultUnit() {
        return defaultUnit;
    }

    public void setDefaultUnit(@NotNull String defaultUnit) {
        this.defaultUnit = defaultUnit;
    }
}