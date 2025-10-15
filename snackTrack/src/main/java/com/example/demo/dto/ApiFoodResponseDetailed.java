package com.example.demo.dto;

import lombok.Data;

@Data
public class ApiFoodResponseDetailed {

    private int id;
    private String name;
    private int calorie;
    private float protein;
    private float carbohydrates;
    private float fat;
    private float defaultWeight;
    private String servingSizeUnit;
    private String brandName;
}
