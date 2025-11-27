package study.snacktrack.dto;

import study.snacktrack.entities.enums.MealNames;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) used for carrying information required to log a user's food consumption.
 * This structure captures which food item was consumed, the amount, the date, and the specific meal context (e.g., LUNCH).
 */
public class RegisteredAlimentationRequest {

    private Integer essentialId;
    private Integer mealApiId;
    private Integer mealId;
    private LocalDate timestamp;
    private MealNames mealName;

    private Float amount;
    private Float pieces;

    /**
     * Getters and setters for the DTO fields.
     * These methods provide standard access and modification capabilities for the registered alimentation details.
     */
    public Integer getEssentialId() {
        return essentialId;
    }

    public void setEssentialId(Integer essentialId) {
        this.essentialId = essentialId;
    }

    public Integer getMealApiId() {
        return mealApiId;
    }

    public Integer getMealId() {
        return mealId;
    }

    public void setMealId(Integer mealId) {
        this.mealId = mealId;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    public MealNames getMealName() {
        return mealName;
    }

    public void setMealName(MealNames mealName) {
        this.mealName = mealName;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Float getPieces() {
        return pieces;
    }

    public void setPieces(Float pieces) {
        this.pieces = pieces;
    }
}