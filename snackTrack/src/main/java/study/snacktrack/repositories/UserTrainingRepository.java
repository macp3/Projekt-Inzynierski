package study.snacktrack.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import study.snacktrack.entities.UserTraining;

/**
 * Repository interface for managing UserTraining entities, which link users to their assigned training plans.
 * This extends JpaRepository to provide standard persistence operations for user-specific training assignments.
 */
@Repository
public interface UserTrainingRepository extends JpaRepository<UserTraining, Integer> {

    /**
     * Retrieves all training plan assignments associated with a specific user ID.
     * This is essential for fetching the list of all routines currently assigned to a particular user.
     *
     * @param userId the ID of the user
     * @return a List of UserTraining entities assigned to the user
     */
    List<UserTraining> findByUserId(int userId);

    /**
     * Deletes all UserTraining records that are associated with a specific training plan ID.
     * This method is used to unassign a training plan from all users before the plan itself is deleted.
     *
     * @param trainingId the ID of the training plan to be unassigned
     */
    void deleteAllByTrainingId(int trainingId);
}