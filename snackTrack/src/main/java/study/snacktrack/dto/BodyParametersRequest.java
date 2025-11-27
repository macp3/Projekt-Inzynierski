package study.snacktrack.dto;

import study.snacktrack.entities.enums.Sex;

/**
 * Data Transfer Object (DTO) used for carrying a user's physical parameters to the service layer.
 * This object contains all necessary data points required to calculate the user's daily nutritional limits.
 */
public class BodyParametersRequest {

    private Sex sex;
    private Float height;
    private Float weight;
    private Integer age;
    private Float dailyActivityFactor;
    private Float dailyActivityTrainingFactor;
    private Float weeklyWeightChangeTempo;
    private Float goalWeight;

    /**
     * Constructs a new BodyParametersRequest object with all required physical metrics.
     * This constructor is used when creating or updating a user's body profile in the system.
     *
     * @param sex                         The user's sex.
     * @param height                      The user's height.
     * @param weight                      The user's current weight.
     * @param age                         The user's age.
     * @param dailyActivityFactor         The user's factor for non-training daily activity.
     * @param dailyActivityTrainingFactor The user's factor for training activity.
     * @param weeklyWeightChangeTempo     The desired weekly weight change tempo.
     * @param goalWeight                  The user's final goal weight.
     */
    public BodyParametersRequest(Sex sex, Float height, Float weight, Integer age, Float dailyActivityFactor,
                                 Float dailyActivityTrainingFactor, Float weeklyWeightChangeTempo, Float goalWeight) {
        this.sex = sex;
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.dailyActivityFactor = dailyActivityFactor;
        this.dailyActivityTrainingFactor = dailyActivityTrainingFactor;
        this.weeklyWeightChangeTempo = weeklyWeightChangeTempo;
        this.goalWeight = goalWeight;
    }

    /**
     * Getters and setters for the DTO fields.
     * These methods allow external access and modification of the user's physical parameters.
     */
    public Sex getSex() {
        return sex;
    }

    public Float getHeight() {
        return height;
    }

    public Float getWeight() {
        return weight;
    }

    public Integer getAge() {
        return age;
    }

    public Float getDailyActivityFactor() {
        return dailyActivityFactor;
    }

    public Float getDailyActivityTrainingFactor() {
        return dailyActivityTrainingFactor;
    }

    public Float getWeeklyWeightChangeTempo() {
        return weeklyWeightChangeTempo;
    }

    public Float getGoalWeight() {
        return goalWeight;
    }
}