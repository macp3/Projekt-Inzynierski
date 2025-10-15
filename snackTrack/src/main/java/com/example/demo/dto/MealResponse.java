package com.example.demo.dto;

import java.util.List;

import lombok.Data;

@Data
public class MealResponse {

    private int id;
    private String name;
    private String description;
    private int authorId;
    private List<IngredientResponse> ingredients;
}
