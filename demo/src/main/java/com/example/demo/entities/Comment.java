package com.example.demo.entities;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

@Entity
@Table (name = "comments")
public class Comment
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @NotNull
    private int id;
    @NotNull
    @Column (name = "author_id")
    private int authorId;
    @NotNull
    private String content;
    @NotNull
    @Column (name = "author_id")
    private int mealId;

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

    @NotNull
    public String getContent() {
        return content;
    }

    public void setContent(@NotNull String content) {
        this.content = content;
    }

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }
}
