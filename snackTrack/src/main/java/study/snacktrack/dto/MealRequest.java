package study.snacktrack.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) used for carrying all necessary information to create a new Meal entity.
 * This structure includes the meal's descriptive data along with a list of all its component ingredients.
 */
public class MealRequest {
    private String name;
    private String description;
    private List<IngredientRequest> ingredients;

    /**
     * Getters and setters for the DTO fields.
     * These methods provide standard access and modification capabilities for the meal's name, description, and ingredient list.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<IngredientRequest> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientRequest> ingredients) {
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}