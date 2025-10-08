package com.example.demo.dto;

import lombok.Data;

@Data
public class ApiFoodResponse {

    private String name;
    private int calorie;
    private float protein;
    private float carbohydrates;
    private float sugar;
    private float fat;
    private int default_weight;
    private String servingSizeUnit;
    private String brandName;
}
