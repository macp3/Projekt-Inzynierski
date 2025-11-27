package study.snacktrack.entities;

import jakarta.persistence.*;

/**
 * Represents a single scheduled exercise within a comprehensive training plan,
 * detailing the structure of the routine.
 * This entity links a specific exercise to a particular training plan ID, its
 * creator, and the sequential day it should be performed.
 */
@Entity
@Table(name = "trainings")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "training_id", nullable = false)
    private Integer trainingId;

    @Column(name = "author_id", nullable = false)
    private Integer authorId;

    @Column(name = "exercise_id", nullable = false)
    private Integer exerciseId;

    @Column(name = "day_of_exercise", nullable = false)
    private Integer dayOfExercise;

    @ManyToOne
    @JoinColumn(name = "training_id", insertable = false, updatable = false)
    private TrainingInfo trainingInfo;

    /**
     * Getters and setters for all entity fields.
     * These methods provide standard access and modification capabilities for the
     * Training entity properties.
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(Integer trainingId) {
        this.trainingId = trainingId;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public Integer getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Integer exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Integer getDayOfExercise() {
        return dayOfExercise;
    }

    public void setDayOfExercise(Integer dayOfExercise) {
        this.dayOfExercise = dayOfExercise;
    }
}