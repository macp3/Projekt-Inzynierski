package study.snacktrack.dto;

import java.util.List;

import study.snacktrack.entities.Exercise;
import study.snacktrack.entities.TrainingInfo;

/**
 * Data Transfer Object (DTO) used for carrying the complete details of a specific training plan.
 * This structure aggregates the general information about the training plan with a structured list of all included exercises and their scheduling.
 */
public class TrainingDetailsResponse {

    private TrainingInfo trainingInfo;
    private List<ExerciseWithDay> exercises;

    /**
     * Inner class DTO used to pair an Exercise entity with the day it is scheduled for.
     * This nested structure is essential for representing the schedule within a multi-day training plan.
     */
    public static class ExerciseWithDay {

        private int dayOfExercise;
        private Exercise exercise;

        /**
         * Getters and setters for the inner class fields.
         * These methods provide standard access and modification capabilities for the exercise and its scheduled day.
         */

        public void setDayOfExercise(int dayOfExercise) {
            this.dayOfExercise = dayOfExercise;
        }

        public Exercise getExercise() {
            return exercise;
        }

        public void setExercise(Exercise exercise) {
            this.exercise = exercise;
        }
    }

    /**
     * Getters and setters for the DTO fields.
     * These methods allow external access and modification of the general training information and the list of scheduled exercises.
     */
    public TrainingInfo getTrainingInfo() {
        return trainingInfo;
    }

    public void setTrainingInfo(TrainingInfo trainingInfo) {
        this.trainingInfo = trainingInfo;
    }

    public List<ExerciseWithDay> getExercises() {
        return exercises;
    }

    public void setExercises(List<ExerciseWithDay> exercises) {
        this.exercises = exercises;
    }
}