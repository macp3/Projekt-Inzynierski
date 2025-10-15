package study.snacktrack.dto;

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
        private Integer durationTime;

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

        public void setDurationTime(Integer durationTime) {
            this.durationTime = durationTime;
        }
    }

    public static class TrainingExercise {

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
