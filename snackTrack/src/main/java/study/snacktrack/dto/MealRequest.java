package study.snacktrack.dto;

import java.util.List;

public class MealRequest {
    //wyrzucilem authorId
    private String name;
    private String description;
    private List<IngredientRequest> ingredients;

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
