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

    public Ingredient() {

    }

    public Ingredient(Meal meal, EssentialFood essentialFood, @Nullable Integer essentialApiId, Float amount,
            Float pieces) {
        this.meal = meal;
        this.essentialFood = essentialFood;
        this.essentialApiId = essentialApiId;
        this.amount = amount;
        this.pieces = pieces;
    }

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

    public void setEssentialFood(EssentialFood essentialFood) {
        this.essentialFood = essentialFood;
    }

    @Nullable
    public Integer getEssentialApiId() {
        return essentialApiId;
    }

    public void setEssentialApiId(@Nullable Integer essentialApiId) {
        this.essentialApiId = essentialApiId;
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
