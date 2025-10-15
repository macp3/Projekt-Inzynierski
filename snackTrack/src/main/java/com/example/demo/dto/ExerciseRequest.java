package com.example.demo.dto;

import java.util.List;

import com.example.demo.entities.Training;

public class ExerciseRequest {

    private String name;
    private String description;
    private String type;
    private int difficulty;
    private int numberOfSets;
    private int repetitionsPerSet;

    public ExerciseRequest(String name, String description, String type, int difficulty, int numberOfSets, int repetitionsPerSet, List<Training> trainings) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.difficulty = difficulty;
        this.numberOfSets = numberOfSets;
        this.repetitionsPerSet = repetitionsPerSet;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getNumberOfSets() {
        return numberOfSets;
    }

    public void setNumberOfSets(int numberOfSets) {
        this.numberOfSets = numberOfSets;
    }

    public int getRepetitionsPerSet() {
        return repetitionsPerSet;
    }

    public void setRepetitionsPerSet(int repetitionsPerSet) {
        this.repetitionsPerSet = repetitionsPerSet;
    }
}
