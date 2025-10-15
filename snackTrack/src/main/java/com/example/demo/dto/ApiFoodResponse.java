package com.example.demo.dto;

import lombok.Data;

@Data
public class ApiFoodResponse {

    private int id;
    private String name;
    private int calorie;
    private float protein;
    private float carbohydrates;
    private float fat;
    private String brandName;
    private String quantity;
}
