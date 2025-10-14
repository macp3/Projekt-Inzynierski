package com.example.demo.dto;

import com.example.demo.entities.Exercise;
import com.example.demo.entities.TrainingInfo;

import java.util.List;

public class TrainingRequest {

    private TreningInfo treningInfo;
    private List<TrainingExercise> trainingExercises;

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

    public static class TreningInfo {
        private String name;
        private String description;
        private int durationTime;

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

        public int getDurationTime() {
            return durationTime;
        }

        public void setDurationTime(int durationTime) {
            this.durationTime = durationTime;
        }
    }

    public static class TrainingExercise
    {
        private int exerciseDay;
        private int exerciseId;

        public int getExerciseDay() {
            return exerciseDay;
        }

        public void setExerciseDay(int exerciseDay) {
            this.exerciseDay = exerciseDay;
        }

        public int getExerciseId() {
            return exerciseId;
        }

        public void setExerciseId(int exerciseId) {
            this.exerciseId = exerciseId;
        }
    }
}

