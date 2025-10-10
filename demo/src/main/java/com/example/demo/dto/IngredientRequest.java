package com.example.demo.dto;

public class IngredientRequest
{
    private Integer essentialFoodId;
    private Integer essentialApiId;
    private float amount;
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

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getDefaultUnit() {
        return defaultUnit;
    }

    public void setDefaultUnit(String defaultUnit) {
        this.defaultUnit = defaultUnit;
    }
}
