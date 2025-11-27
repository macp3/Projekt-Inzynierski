package study.snacktrack.dto;

/**
 * Data Transfer Object (DTO) used for carrying the information required to define a single ingredient within a meal.
 * This object specifies the food source (either internal or external) and the measured quantity used in the recipe.
 */
public class IngredientRequest {

    private Integer essentialFoodId;
    private Integer essentialApiId;
    private Float amount;
    private Float pieces;
    private String defaultUnit;

    /**
     * Getters and setters for the DTO fields.
     * These methods allow external access and modification of the ingredient's identifiers, amount, and unit of measurement.
     */
    public Integer getEssentialFoodId() {
        return essentialFoodId;
    }

    public void setEssentialFoodId(Integer essentialFoodId) {
        this.essentialFoodId = essentialFoodId;
    }

    public Integer getEssentialApiId() {
        return essentialApiId;
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