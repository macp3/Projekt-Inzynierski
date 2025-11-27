package study.snacktrack.entities;

import org.jetbrains.annotations.NotNull;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a record linking a user to a meal they have marked as a favorite.
 * This entity tracks the user's preferred meals, allowing for quick retrieval and personalized recommendations.
 */
@Entity
@Table(name = "favourite")
public class Favourite {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    @Column(name = "user_id")
    private int userId;
    @Column(name = "meal_id")
    @Nullable
    private int mealId;

    /**
     * Getters and setters for all entity fields.
     * These methods provide standard access and modification capabilities for the Favourite entity properties.
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }
}