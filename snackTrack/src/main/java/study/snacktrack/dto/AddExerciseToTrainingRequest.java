package study.snacktrack.dto;

public class AddExerciseToTrainingRequest {

    private int exerciseId;
    private int trainingId;
    private int dayOfExercise;

    public AddExerciseToTrainingRequest(int exerciseId, int trainingId, int dayOfExercise) {
        this.exerciseId = exerciseId;
        this.trainingId = trainingId;
        this.dayOfExercise = dayOfExercise;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
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

    public void setDayOfExercise(int dayOfExercise) {
        this.dayOfExercise = dayOfExercise;
    }
}
