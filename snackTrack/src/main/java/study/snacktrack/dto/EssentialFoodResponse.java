package study.snacktrack.dto;

import study.snacktrack.entities.EssentialFood;

/**
 * Data Transfer Object (DTO) used for carrying complete nutritional and descriptive details of an EssentialFood entity.
 * This structure is typically returned to the client when a food item is retrieved from the internal database.
 */
public class EssentialFoodResponse {

    private int id;
    private String name;
    private String description;
    private float calories;
    private float protein;
    private float fat;
    private float carbohydrates;
    private String brandName;
    private float defaultWeight;
    private String servingSizeUnit;

    /**
     * Constructs a new EssentialFoodResponse object by mapping data from a persistent EssentialFood entity.
     * This constructor simplifies the conversion process from the database model to the external data structure.
     *
     * @param food The EssentialFood entity to map data from.
     */
    public EssentialFoodResponse(EssentialFood food) {
        this.id = food.getId();
        this.name = food.getName();
        this.description = food.getDescription();
        this.calories = food.getCalories();
        this.protein = food.getProtein();
        this.fat = food.getFat();
        this.carbohydrates = food.getCarbohydrates();
        this.brandName = food.getBrandName();
        this.defaultWeight = food.getDefaultWeight();
        this.servingSizeUnit = food.getServingSizeUnit();
    }

    /**
     * Getters and setters for the DTO fields.
     * These methods allow external access and modification of the food item's detailed properties.
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getCalories() {
        return calories;
    }
}