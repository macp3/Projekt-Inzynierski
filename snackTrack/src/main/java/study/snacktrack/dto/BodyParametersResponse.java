package study.snacktrack.dto;

import study.snacktrack.entities.enums.Sex;

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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Float getDailyActivityFactor() {
        return dailyActivityFactor;
    }

    public void setDailyActivityFactor(Float dailyActivityFactor) {
        this.dailyActivityFactor = dailyActivityFactor;
    }

    public Float getDailyActivityTrainingFactor() {
        return dailyActivityTrainingFactor;
    }

    public void setDailyActivityTrainingFactor(Float dailyActivityTrainingFactor) {
        this.dailyActivityTrainingFactor = dailyActivityTrainingFactor;
    }

    public Float getWeeklyWeightChangeTempo() {
        return weeklyWeightChangeTempo;
    }

    public void setWeeklyWeightChangeTempo(Float weeklyWeightChangeTempo) {
        this.weeklyWeightChangeTempo = weeklyWeightChangeTempo;
    }

    public Float getGoalWeight() {
        return goalWeight;
    }

    public void setGoalWeight(Float goalWeight) {
        this.goalWeight = goalWeight;
    }

    public Float getCalorieLimit() {
        return calorieLimit;
    }

    public void setCalorieLimit(Float calorieLimit) {
        this.calorieLimit = calorieLimit;
    }

    public Float getProteinLimit() {
        return proteinLimit;
    }

    public void setProteinLimit(Float proteinLimit) {
        this.proteinLimit = proteinLimit;
    }

    public Float getFatLimit() {
        return fatLimit;
    }

    public void setFatLimit(Float fatLimit) {
        this.fatLimit = fatLimit;
    }

    public Float getCarbohydratesLimit() {
        return carbohydratesLimit;
    }

    public void setCarbohydratesLimit(Float carbohydratesLimit) {
        this.carbohydratesLimit = carbohydratesLimit;
    }
}
