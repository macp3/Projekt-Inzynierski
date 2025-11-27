package study.snacktrack.dto;

import java.util.List;

import study.snacktrack.entities.Training;

/**
 * Data Transfer Object (DTO) used for carrying detailed information required to create or update an Exercise entity.
 * This structure encapsulates all metadata and execution parameters for a new exercise definition.
 */
public class ExerciseRequest {

    private String name;
    private String description;
    private String type;
    private int difficulty;
    private int numberOfSets;
    private int repetitionsPerSet;

    /**
     * Constructs a new ExerciseRequest with all required exercise properties.
     * This constructor is used by the service layer to validate and persist a new exercise item in the application's database.
     *
     * @param name The name of the exercise.
     * @param description A brief description of the exercise.
     * @param type The category or type of the exercise (e.g., cardio, strength).
     * @param difficulty The rating of the exercise difficulty.
     * @param numberOfSets The required number of sets for the exercise.
     * @param repetitionsPerSet The required number of repetitions per set.
     */
    public ExerciseRequest(String name, String description, String type, int difficulty, int numberOfSets, int repetitionsPerSet) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.difficulty = difficulty;
        this.numberOfSets = numberOfSets;
        this.repetitionsPerSet = repetitionsPerSet;
    }

    /**
     * Getters and setters for the DTO fields.
     * These methods allow external access and modification of the exercise's properties and parameters.
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getNumberOfSets() {
        return numberOfSets;
    }

    public int getRepetitionsPerSet() {
        return repetitionsPerSet;
    }
}