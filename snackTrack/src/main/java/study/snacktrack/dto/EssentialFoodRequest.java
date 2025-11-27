package study.snacktrack.dto;

/**
 * Data Transfer Object (DTO) used for carrying detailed information required to create or update an EssentialFood entity.
 * This structure encapsulates the food item's name, description, nutritional breakdown, and serving size data.
 */
public class EssentialFoodRequest
{
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
     * Constructs a new EssentialFoodRequest with all necessary nutritional and descriptive details.
     * This constructor is used by the service layer to validate and persist a new food item in the application's database.
     *
     * @param name The name of the food item.
     * @param description A brief description of the food item.
     * @param calories The caloric content per serving.
     * @param protein The protein content per serving.
     * @param fat The fat content per serving.
     * @param carbohydrates The carbohydrate content per serving.
     * @param brandName The brand name of the food product.
     * @param defaultWeight The default weight of the serving size.
     * @param servingSizeUnit The unit of measurement for the serving size.
     */
    public EssentialFoodRequest(String name, String description, float calories, float protein, float fat, float carbohydrates, String brandName, float defaultWeight, String servingSizeUnit) {
        this.name = name;
        this.description = description;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
        this.brandName = brandName;
        this.defaultWeight = defaultWeight;
        this.servingSizeUnit = servingSizeUnit;
    }

    /**
     * Getters and setters for the DTO fields.
     * These methods allow external access and modification of the food item's detailed properties.
     */
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

    public float getProtein() {
        return protein;
    }

    public float getFat() {
        return fat;
    }

    public float getCarbohydrates() {
        return carbohydrates;
    }

    public String getBrandName() {
        return brandName;
    }

    public float getDefaultWeight() {
        return defaultWeight;
    }

    public String getServingSizeUnit() {
        return servingSizeUnit;
    }
}