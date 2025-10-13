package com.example.demo.entities;

import java.time.LocalDate;

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
@Table(name = "registered_alimentation")
public class RegisteredAlimentation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private int userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "essential_id")
    private EssentialFood essentialFood;

    @Column(name = "meal_api_id")
    private Integer mealApiId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "meal_id")
    private Meal meal;

    @Column(name = "timestamp", nullable = false)
    private LocalDate timestamp;

    @Column(name = "amount")
    private Float amount;

    @Column(name = "pieces")
    private Integer pieces;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EssentialFood getEssentialFood() {
        return essentialFood;
    }

    public void setEssentialFood(EssentialFood essentialFood) {
        this.essentialFood = essentialFood;
    }

    public Integer getMealApiId() {
        return mealApiId;
    }

    public void setMealApiId(Integer mealApiId) {
        this.mealApiId = mealApiId;
    }

    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Integer getPieces() {
        return pieces;
    }

    public void setPieces(Integer pieces) {
        this.pieces = pieces;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
