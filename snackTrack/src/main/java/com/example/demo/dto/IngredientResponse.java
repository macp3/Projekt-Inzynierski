package com.example.demo.dto;

import com.example.demo.entities.Ingredient;

import lombok.Data;

@Data
public class IngredientResponse {

    private int id;
    private Float amount;
    private Integer pieces;
    private ApiFoodResponseDetailed essentialApi;
    private EssentialFoodResponse essentialFood;

    public IngredientResponse() {
    }

    public IngredientResponse(Ingredient ingredient) {
        this.id = ingredient.getId();
        this.amount = ingredient.getAmount();
        this.pieces = ingredient.getPieces();
        this.essentialApi = null;
        this.essentialFood = new EssentialFoodResponse(ingredient.getEssentialFood());
    }
}
