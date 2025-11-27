package study.snacktrack.entities;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import study.snacktrack.dto.ApiFoodResponseDetailed;
import org.jetbrains.annotations.NotNull;

import study.snacktrack.services.FoodService;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Represents a complex culinary recipe or dish created and stored by a user.
 * This entity is composed of multiple Ingredient entities and serves as a
 * blueprint for logged food consumption.
 */
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
    @Column(name = "image_url")
    @Nullable
    private String imageUrl;

    /**
     * Default constructor required by JPA and Hibernate for entity instantiation.
     * It initializes the ingredients list to ensure it is never null when the
     * entity is managed by the persistence context.
     */
    public Meal() {
        this.ingredients = new ArrayList<>();

    }

    /**
     * Constructs a new Meal entity with its descriptive properties and author ID.
     * This constructor is used when a user initially defines a new recipe.
     *
     * @param authorId    The ID of the user who created the meal.
     * @param name        The name of the meal.
     * @param description A brief description of the meal.
     */
    public Meal(int authorId, @NotNull String name, @NotNull String description) {
        this.ingredients = new ArrayList<>();
        this.authorId = authorId;
        this.name = name;
        this.description = description;
    }

    /**
     * Adds a new ingredient to this meal and establishes the bidirectional
     * relationship.
     * This utility method simplifies the process of building the meal's recipe
     * structure.
     *
     * @param ingredient The Ingredient entity to be added.
     */
    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
        ingredient.setMeal(this);
    }

    /**
     * Getters and setters for the entity fields.
     * These methods provide standard access and modification capabilities for the
     * Meal entity properties.
     */
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

    /**
     * Calculates the total caloric content of the entire meal by summing the
     * calories of all its ingredients.
     * This method iterates through all ingredients, fetching external data when
     * necessary, to compute the final nutritional value.
     *
     * @param foodService The service dependency used for retrieving external food
     *                    data by API ID.
     * @return The total caloric value of the meal as a float.
     */
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}