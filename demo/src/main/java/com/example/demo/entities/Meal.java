package com.example.demo.entities;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.example.demo.dto.ApiFoodResponseDetailed;
import com.example.demo.services.FoodService;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "meals")
public class Meal {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    @Column(name = "author_id")
    private int authorId;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Ingredient> ingredients;

    public Meal() {
        this.ingredients = new ArrayList<>();

    }

    public Meal(int authorId, @NotNull String name, @NotNull String description) {
        this.ingredients = new ArrayList<>();
        this.authorId = authorId;
        this.name = name;
        this.description = description;
    }

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
        ingredient.setMeal(this);
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
    public String getDescription() {
        return description;
    }

    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public float getTotalCalories(FoodService foodService) {
        float total = 0f;

        for (Ingredient ingredient : ingredients) {
            float ingredientCalories = 0f;

            if (ingredient.getEssentialFood() != null) {
                ingredientCalories = ingredient.getEssentialFood().getCalories();
                if (ingredient.getAmount() != null) {
                    ingredientCalories *= (ingredient.getAmount() / ingredient.getEssentialFood().getDefaultWeight());
                } else if (ingredient.getPieces() != null) {
                    ingredientCalories *= ingredient.getPieces();
                }
            } else if (ingredient.getEssentialApiId() != null) {
                ApiFoodResponseDetailed apiFood = foodService.getFoodFromApiById(ingredient.getEssentialApiId());
                ingredientCalories = apiFood.getCalorie();

                if (ingredient.getAmount() != null) {
                    ingredientCalories *= (ingredient.getAmount() / apiFood.getDefaultWeight());
                } else if (ingredient.getPieces() != null) {
                    ingredientCalories *= ingredient.getPieces();
                }
            }

            total += ingredientCalories;
        }

        return total;
    }
}
