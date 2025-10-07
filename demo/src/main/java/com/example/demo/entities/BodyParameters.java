package com.example.demo.entities;

import com.example.demo.entities.enums.Sex;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "body_parameters")
public class BodyParameters
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @NotNull
    private int userId;
    @Enumerated(EnumType.STRING)
    @NotNull
    private Sex sex;
    @NotNull
    private float height;
    @NotNull
    private float weight;
    @NotNull
    private int age;
    @Column(name = "daily_activity_factor")
    @NotNull
    private float dailyActivityFactor;
    @Column(name = "daily_activity_training_factor")
    @NotNull
    private float dailyActivityTrainingFactor;
    @Column(name = "weekly_weight_change_tempo")
    @NotNull
    private float weeklyWeightChangeTempo;
    @Column(name = "goal_weight")
    @NotNull
    private float goalWeight;
    @Column(name = "calorie_limit")
    @NotNull
    private float calorieLimit;
    @Column(name = "protein_limit")
    @NotNull
    private float proteinLimit;
    @Column(name = "fat_limit")
    @NotNull
    private float fatLimit;
    @Column(name = "carbohydrates_limit")
    @NotNull
    private float carbohydratesLimit;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @NotNull
    public Sex getSex() {
        return sex;
    }

    public void setSex(@NotNull Sex sex) {
        this.sex = sex;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getDailyActivityFactor() {
        return dailyActivityFactor;
    }

    public void setDailyActivityFactor(float dailyActivityFactor) {
        this.dailyActivityFactor = dailyActivityFactor;
    }

    public float getDailyActivityTrainingFactor() {
        return dailyActivityTrainingFactor;
    }

    public void setDailyActivityTrainingFactor(float dailyActivityTrainingFactor) {
        this.dailyActivityTrainingFactor = dailyActivityTrainingFactor;
    }

    public float getGoalWeight() {
        return goalWeight;
    }

    public void setGoalWeight(float goalWeight) {
        this.goalWeight = goalWeight;
    }

    public float getCalorieLimit() {
        return calorieLimit;
    }

    public void setCalorieLimit(float calorieLimit) {
        this.calorieLimit = calorieLimit;
    }

    public float getProteinLimit() {
        return proteinLimit;
    }

    public void setProteinLimit(float proteinLimit) {
        this.proteinLimit = proteinLimit;
    }

    public float getFatLimit() {
        return fatLimit;
    }

    public void setFatLimit(float fatLimit) {
        this.fatLimit = fatLimit;
    }

    public float getCarbohydratesLimit() {
        return carbohydratesLimit;
    }

    public void setCarbohydratesLimit(float carbohydratesLimit) {
        this.carbohydratesLimit = carbohydratesLimit;
    }

    public float getWeeklyWeightChangeTempo() {
        return weeklyWeightChangeTempo;
    }

    public void setWeeklyWeightChangeTempo(float weeklyWeightChangeTempo) {
        this.weeklyWeightChangeTempo = weeklyWeightChangeTempo;
    }
}
