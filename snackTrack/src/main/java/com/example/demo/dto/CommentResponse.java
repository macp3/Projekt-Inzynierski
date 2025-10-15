package com.example.demo.dto;

public class CommentResponse {

    private int id;
    private int authorId;
    private String content;
    private int mealId;

    public CommentResponse(int id, int authorId, String content, int mealId) {
        this.id = id;
        this.authorId = authorId;
        this.content = content;
        this.mealId = mealId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }
}
