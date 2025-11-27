package study.snacktrack.entities;

import java.time.LocalDate;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a record of a specific user being assigned or starting a particular training program on a given date.
 * This entity uses a composite key based on user ID and training ID to link the user to the plan and tracks the timestamp of assignment.
 */
@Entity
@Table(name = "user_trainings")
@IdClass(UserTrainingId.class)
public class UserTraining {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @NotNull
    private int userId;
    @Id
    @Column(name = "training_id")
    @NotNull
    private int trainingId;
    @NotNull
    private LocalDate timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_id", insertable = false, updatable = false)
    private TrainingInfo trainingInfo;


    /**
     * Getters and setters for all entity fields.
     * These methods provide standard access and modification capabilities for the UserTraining entity properties.
     */
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(int trainingId) {
        this.trainingId = trainingId;
    }

    public void setTimestamp(@NotNull LocalDate timestamp) {
        this.timestamp = timestamp;
    }
}