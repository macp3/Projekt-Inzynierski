package study.snacktrack.dto;

import lombok.Data;
import study.snacktrack.entities.Exercise;
import study.snacktrack.entities.TrainingInfo;

import java.util.List;

@Data
public class TrainingDetailsResponse {

    private TrainingInfo trainingInfo;
    private List<ExerciseWithDay> exercises;

    @Data
    public static class ExerciseWithDay {
        private int dayOfExercise;
        private Exercise exercise;
    }
}