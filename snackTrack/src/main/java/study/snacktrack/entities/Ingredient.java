package study.snacktrack.entities;

import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Represents a specific component food item used within a larger Meal entity.
 * This entity links a meal to a food source (internal database or external API) and records the quantity used.
 */
@Entity
@Table(name = "ingredients")
public class Ingredient {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id")
    @JsonBackReference
    private Meal meal;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "essential_id")
    private EssentialFood essentialFood;
    @Column(name = "essential_api_id")
    @Nullable
    private Integer essentialApiId;
    @Nullable
    private Float amount;
    @Nullable
    private Float pieces;

    /**
     * Constructs a new Ingredient entity linking a meal to a food source and recording the consumed quantity.
     * This constructor is typically used when assembling a new meal recipe in the application.
     *
     * @param meal The Meal entity this ingredient belongs to.
     * @param essentialFood The internal food entity used as the source, if applicable.
     * @param essentialApiId The ID of the external API food source, if applicable.
     * @param amount The numerical quantity of the ingredient used (e.g., in grams or milliliters).
     * @param pieces The number of separate pieces or units used.
     */
    public Ingredient(Meal meal, EssentialFood essentialFood, @Nullable Integer essentialApiId, Float amount, Float pieces) {
        this.meal = meal;
        this.essentialFood = essentialFood;
        this.essentialApiId = essentialApiId;
        this.amount = amount;
        this.pieces = pieces;
    }

    /**
     * Getters and setters for all entity fields.
     * These methods provide standard access and modification capabilities for the Ingredient entity properties.
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    public EssentialFood getEssentialFood() {
        return essentialFood;
    }

    @Nullable
    public Integer getEssentialApiId() {
        return essentialApiId;
    }
    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    @Nullable
    public Float getPieces() {
        return pieces;
    }

    public void setPieces(@Nullable Float pieces) {
        this.pieces = pieces;
    }
}