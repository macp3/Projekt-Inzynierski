package com.example.demo.dto;

import com.example.demo.entities.Ingredient;

import lombok.Data;

@Data
public class IngredientResponse {

    private int id;
    private float amount;
    private String defaultUnit;
    private Integer essentialApiId;
    private EssentialFoodResponse essentialFood;

    public IngredientResponse() {
    }

    public IngredientResponse(Ingredient ingredient) {
        this.id = ingredient.getId();
        this.amount = ingredient.getAmount();
        this.defaultUnit = ingredient.getDefaultUnit();
        this.essentialApiId = ingredient.getEssentialApiId();
        this.essentialFood = new EssentialFoodResponse(ingredient.getEssentialFood());
    }
}
