package study.snacktrack.dto;

import study.snacktrack.entities.enums.Sex;

/**
 * Data Transfer Object (DTO) used for carrying a user's calculated nutritional limits and body parameters back to the client.
 * This structure contains both the initial metrics provided by the user and the resulting daily macronutrient and calorie goals.
 */
public class BodyParametersResponse {

    private int userId;
    private Sex sex;
    private Float height;
    private Float weight;
    private Integer age;
    private Float dailyActivityFactor;
    private Float dailyActivityTrainingFactor;
    private Float weeklyWeightChangeTempo;
    private Float goalWeight;
    private Float calorieLimit;
    private Float proteinLimit;
    private Float fatLimit;
    private Float carbohydratesLimit;

    /**
     * Constructs a new BodyParametersResponse object with all calculated and input values.
     * This comprehensive constructor is used by the service layer to encapsulate the complete results of a body parameter calculation.
     *
     * @param userId The ID of the user.
     * @param sex The user's sex.
     * @param height The user's height.
     * @param weight The user's current weight.
     * @param age The user's age.
     * @param dailyActivityFactor The user's factor for non-training daily activity.
     * @param dailyActivityTrainingFactor The user's factor for training activity.
     * @param weeklyWeightChangeTempo The desired weekly weight change tempo.
     * @param goalWeight The user's final goal weight.
     * @param calorieLimit The calculated daily calorie limit.
     * @param proteinLimit The calculated daily protein limit.
     * @param fatLimit The calculated daily fat limit.
     * @param carbohydratesLimit The calculated daily carbohydrates limit.
     */
    public BodyParametersResponse(int userId, Sex sex, Float height, Float weight, Integer age,
                                  Float dailyActivityFactor, Float dailyActivityTrainingFactor, Float weeklyWeightChangeTempo,
                                  Float goalWeight, Float calorieLimit, Float proteinLimit, Float fatLimit, Float carbohydratesLimit) {
        this.userId = userId;
        this.sex = sex;
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.dailyActivityFactor = dailyActivityFactor;
        this.dailyActivityTrainingFactor = dailyActivityTrainingFactor;
        this.weeklyWeightChangeTempo = weeklyWeightChangeTempo;
        this.goalWeight = goalWeight;
        this.calorieLimit = calorieLimit;
        this.proteinLimit = proteinLimit;
        this.fatLimit = fatLimit;
        this.carbohydratesLimit = carbohydratesLimit;
    }

    /**
     * Getters and setters for the DTO fields.
     * These methods allow external access and modification of the user's body metrics and calculated nutritional limits.
     */
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Float getCalorieLimit() {
        return calorieLimit;
    }
}