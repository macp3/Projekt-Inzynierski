package com.example.demo.dto;

import com.example.demo.entities.enums.Sex;
import org.springframework.web.bind.annotation.RequestParam;

public class BodyParametersRequest
{
    private Sex sex;
    private Float height;
    private Float weight;
    private Integer age;
    private Float dailyActivityFactor;
    private Float dailyActivityTrainingFactor;
    private Float weeklyWeightChangeTempo;
    private Float goalWeight;

    public BodyParametersRequest(Sex sex, Float height, Float weight, Integer age, Float dailyActivityFactor, Float dailyActivityTrainingFactor, Float weeklyWeightChangeTempo, Float goalWeight) {
        this.sex = sex;
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.dailyActivityFactor = dailyActivityFactor;
        this.dailyActivityTrainingFactor = dailyActivityTrainingFactor;
        this.weeklyWeightChangeTempo = weeklyWeightChangeTempo;
        this.goalWeight = goalWeight;
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
}
