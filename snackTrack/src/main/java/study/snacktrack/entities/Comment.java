package study.snacktrack.entities;

import org.jetbrains.annotations.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a user comment posted on a meal within the application.
 * This entity stores the comment's content, its author, the meal it relates to, and the current number of likes received.
 */
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private int id;
    @NotNull
    @Column(name = "author_id")
    private int authorId;
    @NotNull
    private String content;
    @NotNull
    @Column(name = "meal_id")
    private int mealId;

    @Column(columnDefinition = "integer default 0")
    private int likes = 0;

    /**
     * Getters and setters for all entity fields.
     * These methods provide standard access and modification capabilities for the Comment entity properties.
     */
    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
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