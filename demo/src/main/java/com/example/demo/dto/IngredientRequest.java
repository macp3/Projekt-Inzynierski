package com.example.demo.dto;

public class IngredientRequest {

    private Integer essentialFoodId;
    private Integer essentialApiId;
    private Float amount;
    private Integer pieces;
    private String defaultUnit;

    public Integer getEssentialFoodId() {
        return essentialFoodId;
    }

    public void setEssentialFoodId(Integer essentialFoodId) {
        this.essentialFoodId = essentialFoodId;
    }

    public Integer getEssentialApiId() {
        return essentialApiId;
    }

    public void setEssentialApiId(Integer essentialApiId) {
        this.essentialApiId = essentialApiId;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getDefaultUnit() {
        return defaultUnit;
    }

    public void setDefaultUnit(String defaultUnit) {
        this.defaultUnit = defaultUnit;
    }

    public Integer getPieces() {
        return pieces;
    }

    public void setPieces(int pieces) {
        this.pieces = pieces;
    }
}
