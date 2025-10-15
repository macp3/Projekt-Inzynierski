package com.example.demo.dto;

import com.example.demo.entities.EssentialFood;

public class EssentialFoodResponse {

    private int id;
    private String name;
    private String description;
    private float calories;
    private float protein;
    private float fat;
    private float carbohydrates;
    private String brandName;
    private float defaultWeight;
    private String servingSizeUnit;

    public EssentialFoodResponse(EssentialFood food) {
        this.id = food.getId();
        this.name = food.getName();
        this.description = food.getDescription();
        this.calories = food.getCalories();
        this.protein = food.getProtein();
        this.fat = food.getFat();
        this.carbohydrates = food.getCarbohydrates();
        this.brandName = food.getBrandName();
        this.defaultWeight = food.getDefaultWeight();
        this.servingSizeUnit = food.getServingSizeUnit();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
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

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public float getDefaultWeight() {
        return defaultWeight;
    }

    public void setDefaultWeight(float defaultWeight) {
        this.defaultWeight = defaultWeight;
    }

    public String getServingSizeUnit() {
        return servingSizeUnit;
    }

    public void setServingSizeUnit(String servingSizeUnit) {
        this.servingSizeUnit = servingSizeUnit;
    }
}
