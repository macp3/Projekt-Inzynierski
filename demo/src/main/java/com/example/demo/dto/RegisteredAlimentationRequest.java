/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.dto;

import java.time.LocalDate;

public class RegisteredAlimentationRequest {

    private Integer essentialId;
    private Integer mealApiId;
    private Integer mealId;
    private LocalDate timestamp;
    private Float amount;
    private Integer pieces;

    public Integer getEssentialId() {
        return essentialId;
    }

    public void setEssentialId(Integer essentialId) {
        this.essentialId = essentialId;
    }

    public Integer getMealApiId() {
        return mealApiId;
    }

    public void setMealApiId(Integer mealApiId) {
        this.mealApiId = mealApiId;
    }

    public Integer getMealId() {
        return mealId;
    }

    public void setMealId(Integer mealId) {
        this.mealId = mealId;
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
}
