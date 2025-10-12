package com.example.demo.dto;

public class ReportedMealRequest
{
    private int mealId;
    private String content;

    public ReportedMealRequest(int mealId, String content) {
        this.mealId = mealId;
        this.content = content;
    }

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
