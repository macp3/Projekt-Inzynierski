package com.example.demo.dto;

import java.time.LocalDate;

import com.example.demo.entities.Meal;
import com.example.demo.entities.RegisteredAlimentation;

public class RegisteredAlimentationResponse {

    private int id;
    private int userId;
    private EssentialFoodResponse essentialFood;
    private ApiFoodResponseDetailed mealApi;
    private Meal meal;
    private LocalDate timestamp;
    private Float amount;
    private Integer pieces;

    public RegisteredAlimentationResponse() {
    }

    public RegisteredAlimentationResponse(RegisteredAlimentation ra) {
        this.id = ra.getId();
        this.userId = ra.getUserId();
        this.timestamp = ra.getTimestamp();
        this.amount = ra.getAmount();
        this.pieces = ra.getPieces();
        this.meal = ra.getMeal();

        if (ra.getEssentialFood() != null) {
            this.essentialFood = new EssentialFoodResponse(ra.getEssentialFood());
        }
    }

    // gettery i settery
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

    public EssentialFoodResponse getEssentialFood() {
        return essentialFood;
    }

    public void setEssentialFood(EssentialFoodResponse essentialFood) {
        this.essentialFood = essentialFood;
    }

    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Integer getPieces() {
        return pieces;
    }

    public void setPieces(Integer pieces) {
        this.pieces = pieces;
    }

    public void setMealApi(ApiFoodResponseDetailed foodFromApiById) {
        this.mealApi = foodFromApiById;
    }

    public ApiFoodResponseDetailed getMealApi() {
        return mealApi;
    }
}
