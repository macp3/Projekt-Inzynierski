package com.example.demo.dto;

import jakarta.persistence.Column;

public class ExerciseDetailsResponse
{
    private Integer id;
    private String name;
    private String description;
    private String type;
    private Integer difficulty;
    private Integer numberOfSets;
    private Integer repetitionsPerSet;

    public ExerciseDetailsResponse(Integer id, String name, String description, String type, Integer difficulty, Integer numberOfSets, Integer repetitionsPerSet) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.difficulty = difficulty;
        this.numberOfSets = numberOfSets;
        this.repetitionsPerSet = repetitionsPerSet;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getNumberOfSets() {
        return numberOfSets;
    }

    public void setNumberOfSets(Integer numberOfSets) {
        this.numberOfSets = numberOfSets;
    }

    public Integer getRepetitionsPerSet() {
        return repetitionsPerSet;
    }

    public void setRepetitionsPerSet(Integer repetitionsPerSet) {
        this.repetitionsPerSet = repetitionsPerSet;
    }
}
