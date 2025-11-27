package study.snacktrack.dto;

/**
 * Data Transfer Object (DTO) used for requesting the addition of an exercise to a specific training plan.
 * This structure holds all necessary identifiers to create a new entry in the Training entity table.
 */
public class AddExerciseToTrainingRequest {

    private int exerciseId;
    private int trainingId;
    private int dayOfExercise;

    /**
     * Constructs a new AddExerciseToTrainingRequest object.
     * This constructor initializes the DTO with the identifiers for the exercise, the training plan, and the designated day.
     *
     * @param exerciseId    The ID of the Exercise to be added.
     * @param trainingId    The ID of the Training plan where the exercise will be placed.
     * @param dayOfExercise The specific day of the training cycle (e.g., 1, 2, 3) when this exercise should be performed.
     */
    public AddExerciseToTrainingRequest(int exerciseId, int trainingId, int dayOfExercise) {
        this.exerciseId = exerciseId;
        this.trainingId = trainingId;
        this.dayOfExercise = dayOfExercise;
    }

    /**
     * Getters and setters for the DTO fields.
     * These methods allow external access and modification of the exercise, training plan, and day identifiers.
     */
    public int getExerciseId() {
        return exerciseId;
    }

    public int getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(int trainingId) {
        this.trainingId = trainingId;
    }

    public int getDayOfExercise() {
        return dayOfExercise;
    }
}