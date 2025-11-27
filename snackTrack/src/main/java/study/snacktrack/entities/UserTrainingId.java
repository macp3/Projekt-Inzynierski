package study.snacktrack.entities;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents the composite key class for the UserTraining entity, adhering to
 * JPA requirements for primary key classes.
 * This class combines the user ID and the training ID to uniquely identify an
 * association between a user and a training plan.
 */
public class UserTrainingId implements Serializable {

    private int userId;
    private int trainingId;

    public UserTrainingId() {
    }

    public UserTrainingId(int userId, int trainingId) {
        this.userId = userId;
        this.trainingId = trainingId;
    }

    /**
     * Compares this composite key object to another object for equality.
     * This method is mandated by JPA for composite keys and ensures two key
     * instances are considered equal if both component IDs match.
     *
     * @param o The object to compare with.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserTrainingId))
            return false;
        UserTrainingId that = (UserTrainingId) o;
        return userId == that.userId && trainingId == that.trainingId;
    }

    /**
     * Generates a hash code value for this composite key object.
     * This method is required alongside equals() by JPA for efficient storage and
     * retrieval in hash-based collections.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, trainingId);
    }

    /**
     * Standard getters and setters for the key fields.
     * These methods are implicitly required by JPA to access and modify the
     * components of the composite key.
     */
}