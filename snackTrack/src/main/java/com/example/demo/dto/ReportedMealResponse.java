package com.example.demo.dto;

public class ReportedMealResponse {

    private int id;
    private int reportingId;
    private int mealId;
    private String content;

    public ReportedMealResponse(int id, int reportingId, int mealId, String content) {
        this.id = id;
        this.reportingId = reportingId;
        this.mealId = mealId;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReportingId() {
        return reportingId;
    }

    public void setReportingId(int reportingId) {
        this.reportingId = reportingId;
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
