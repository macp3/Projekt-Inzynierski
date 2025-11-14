package study.snacktrack.entities;

import study.snacktrack.entities.enums.Sex;
import org.jetbrains.annotations.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "body_parameters")
public class BodyParameters {

    @Id
    @Column(name = "user_id")
    @NotNull
    private Integer userId;
    @Enumerated(EnumType.STRING)
    @NotNull
    private Sex sex;
    @NotNull
    private Float height;
    @NotNull
    private Float weight;
    @NotNull
    private Integer age;
    @Column(name = "daily_activity_factor")
    @NotNull
    private Float dailyActivityFactor;
    @Column(name = "daily_activity_training_factor")
    @NotNull
    private Float dailyActivityTrainingFactor;
    @Column(name = "weekly_weight_change_tempo")
    @NotNull
    private Float weeklyWeightChangeTempo;
    @Column(name = "goal_weight")
    @NotNull
    private Float goalWeight;
    @Column(name = "calorie_limit")
    @NotNull
    private Float calorieLimit;
    @Column(name = "protein_limit")
    @NotNull
    private Float proteinLimit;
    @Column(name = "fat_limit")
    @NotNull
    private Float fatLimit;
    @Column(name = "carbohydrates_limit")
    @NotNull
    private Float carbohydratesLimit;

    @NotNull
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(@NotNull Integer userId) {
        this.userId = userId;
    }

    @NotNull
    public Sex getSex() {
        return sex;
    }

    public void setSex(@NotNull Sex sex) {
        this.sex = sex;
    }

    @NotNull
    public Float getHeight() {
        return height;
    }

    public void setHeight(@NotNull Float height) {
        this.height = height;
    }

    @NotNull
    public Float getWeight() {
        return weight;
    }

    public void setWeight(@NotNull Float weight) {
        this.weight = weight;
    }

    @NotNull
    public Integer getAge() {
        return age;
    }

    public void setAge(@NotNull Integer age) {
        this.age = age;
    }

    @NotNull
    public Float getDailyActivityFactor() {
        return dailyActivityFactor;
    }

    public void setDailyActivityFactor(@NotNull Float dailyActivityFactor) {
        this.dailyActivityFactor = dailyActivityFactor;
    }

    @NotNull
    public Float getDailyActivityTrainingFactor() {
        return dailyActivityTrainingFactor;
    }

    public void setDailyActivityTrainingFactor(@NotNull Float dailyActivityTrainingFactor) {
        this.dailyActivityTrainingFactor = dailyActivityTrainingFactor;
    }

    @NotNull
    public Float getWeeklyWeightChangeTempo() {
        return weeklyWeightChangeTempo;
    }

    public void setWeeklyWeightChangeTempo(@NotNull Float weeklyWeightChangeTempo) {
        this.weeklyWeightChangeTempo = weeklyWeightChangeTempo;
    }

    @NotNull
    public Float getGoalWeight() {
        return goalWeight;
    }

    public void setGoalWeight(@NotNull Float goalWeight) {
        this.goalWeight = goalWeight;
    }

    @NotNull
    public Float getCalorieLimit() {
        return calorieLimit;
    }

    public void setCalorieLimit(@NotNull Float calorieLimit) {
        this.calorieLimit = calorieLimit;
    }

    @NotNull
    public Float getProteinLimit() {
        return proteinLimit;
    }

    public void setProteinLimit(@NotNull Float proteinLimit) {
        this.proteinLimit = proteinLimit;
    }

    @NotNull
    public Float getFatLimit() {
        return fatLimit;
    }

    public void setFatLimit(@NotNull Float fatLimit) {
        this.fatLimit = fatLimit;
    }

    @NotNull
    public Float getCarbohydratesLimit() {
        return carbohydratesLimit;
    }

    public void setCarbohydratesLimit(@NotNull Float carbohydratesLimit) {
        this.carbohydratesLimit = carbohydratesLimit;
    }
}
