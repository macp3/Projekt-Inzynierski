package study.snacktrack.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) used for carrying all necessary data to create a complete new training plan.
 * This structure combines the general plan information with the specific exercises scheduled within the routine.
 */
public class TrainingRequest {

    private TreningInfo treningInfo;
    private List<TrainingExercise> trainingExercises;

    /**
     * Getters and setters for the main DTO fields.
     * These methods provide standard access and modification capabilities for the training plan details and exercise list.
     */
    public TreningInfo getTreningInfo() {
        return treningInfo;
    }

    public void setTreningInfo(TreningInfo treningInfo) {
        this.treningInfo = treningInfo;
    }

    public List<TrainingExercise> getTrainingExercises() {
        return trainingExercises;
    }

    public void setTrainingExercises(List<TrainingExercise> trainingExercises) {
        this.trainingExercises = trainingExercises;
    }

    /**
     * Inner class DTO used to encapsulate the descriptive information for the entire training plan.
     * This structure contains the plan's title, description, and total intended duration.
     */
    public static class TreningInfo {

        private String name;
        private String description;
        private Integer durationTime;

        /**
         * Getters and setters for the training plan information fields.
         * These methods provide standard access and modification capabilities for the name, description, and duration.
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

        public Integer getDurationTime() {
            return durationTime;
        }
    }

    /**
     * Inner class DTO used to link an existing exercise to a specific day within the new training plan.
     * This structure identifies the exercise and its scheduled sequence day.
     */
    public static class TrainingExercise {

        private Integer exerciseDay;
        private Integer exerciseId;

        /**
         * Getters and setters for the training exercise fields.
         * These methods provide standard access and modification capabilities for the scheduled day and exercise identifier.
         */
        public Integer getExerciseDay() {
            return exerciseDay;
        }
        public Integer getExerciseId() {
            return exerciseId;
        }
    }
}