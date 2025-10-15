package com.example.demo.dto;

import java.util.List;

import com.example.demo.entities.Exercise;
import com.example.demo.entities.TrainingInfo;

public class TrainingDetailsResponse {

    private TrainingInfo trainingInfo;
    private List<ExerciseWithDay> exercises;

    public static class ExerciseWithDay {

        private int dayOfExercise;
        private Exercise exercise;

        public int getDayOfExercise() {
            return dayOfExercise;
        }

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
