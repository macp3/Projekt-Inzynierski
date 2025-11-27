package study.snacktrack.entities;

import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a basic, essential food item stored in the application's internal database.
 * This entity contains the nutritional breakdown (macros and calories) and descriptive details for one specific food source.
 */
@Entity
@Table(name = "essential_food")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EssentialFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private int id;
    @NotNull
    @Column(unique = true)
    private String name;
    @NotNull
    @Column(name = "author_id")
    private int authorId;
    @NotNull
    private String description;
    @NotNull
    private float calories;
    @NotNull
    private float protein;
    @NotNull
    private float fat;
    @NotNull
    private float carbohydrates;
    @Column(name = "serving_size_unit")
    private String servingSizeUnit;
    @Column(name = "default_weight")
    private float defaultWeight;
    @Column(name = "brand_name")
    private String brandName;

    /**
     * Default constructor required by JPA and Hibernate for entity instantiation.
     * This empty constructor ensures the ORM framework can correctly load and manage the entity lifecycle.
     */
    public EssentialFood() {
    }

    /**
     * Constructs a new EssentialFood entity with all necessary nutritional and descriptive details.
     * This parameterized constructor is typically used when saving a new user-defined food item to the database.
     *
     * @param id The unique ID of the food item.
     * @param name The unique name of the food item.
     * @param authorId The ID of the user who created this food item record.
     * @param description A brief description of the food item.
     * @param calories The caloric content per serving.
     * @param protein The protein content per serving.
     * @param fat The fat content per serving.
     * @param carbohydrates The carbohydrate content per serving.
     * @param servingSizeUnit The unit of measurement for the serving size.
     * @param defaultWeight The default weight of the serving size.
     * @param brandName The brand name of the food product.
     */
    public EssentialFood(int id, @NotNull String name, int authorId, @NotNull String description, float calories, float protein, float fat, float carbohydrates, String servingSizeUnit, float defaultWeight, String brandName) {
        this.id = id;
        this.name = name;
        this.authorId = authorId;
        this.description = description;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
        this.servingSizeUnit = servingSizeUnit;
        this.defaultWeight = defaultWeight;
        this.brandName = brandName;
    }

    /**
     * Getters and setters for all entity fields.
     * These methods provide standard access and modification capabilities for the EssentialFood entity properties.
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
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

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getFat() {
        return fat;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    public float getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(float carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public float getDefaultWeight() {
        return defaultWeight;
    }

    public void setDefaultWeight(float defaultWeight) {
        this.defaultWeight = defaultWeight;
    }

    public String getServingSizeUnit() {
        return servingSizeUnit;
    }

    public void setServingSizeUnit(String servingSizeUnit) {
        this.servingSizeUnit = servingSizeUnit;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
}