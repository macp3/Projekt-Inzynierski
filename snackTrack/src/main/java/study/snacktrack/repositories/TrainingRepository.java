package study.snacktrack.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import study.snacktrack.entities.Training;

/**
 * Repository interface for managing Training entities, which represent individual exercise entries within a training plan.
 * This extends JpaRepository to handle standard persistence operations and specialized querying for training components.
 */
@Repository
public interface TrainingRepository extends JpaRepository<Training, Integer> {

    /**
     * Finds a specific Training entity by its primary key ID.
     * This method retrieves a single record from the repository, wrapped in an Optional for null safety.
     *
     * @param trainingId the primary key ID of the Training record
     * @return an Optional containing the Training entity
     */
    Optional<Training> findById(int trainingId);

    /**
     * Retrieves all Training entries associated with a specific logical training plan ID.
     * This method is typically used to fetch the complete list of exercises that make up a whole training routine.
     *
     * @param trainingId the ID of the training plan
     * @return a List of Training entities belonging to the specified plan
     */
    List<Training> findByTrainingId(int trainingId);

    /**
     * Checks for the existence of a specific exercise entry within a training plan on a certain day.
     * This is used to validate that a unique combination of training plan, exercise, and day does not already exist.
     *
     * @param trainingId the ID of the training plan
     * @param exerciseId the ID of the exercise
     * @param dayOfExercise the day number the exercise is scheduled for
     * @return true if an entry with the given composite key exists, false otherwise
     */
    boolean existsByTrainingIdAndExerciseIdAndDayOfExercise(int trainingId, int exerciseId, int dayOfExercise);

    /**
     * Retrieves all Training entries for a specific exercise within a particular training plan.
     * This method allows fetching all scheduled instances of a single exercise across all days of the given training.
     *
     * @param trainingId the ID of the training plan
     * @param exerciseId the ID of the exercise
     * @return a List of Training entities matching the specified plan and exercise
     */
    List<Training> findAllByTrainingIdAndExerciseId(int trainingId, int exerciseId);

    /**
     * Deletes all Training entries that belong to a specific training plan ID.
     * This method is executed when an entire training routine needs to be removed from the database.
     *
     * @param trainingId the ID of the training plan whose entries should be deleted
     */
    void deleteAllByTrainingId(int trainingId);

    /**
     * Retrieves a single Training entry defined by the combination of plan ID, exercise ID, and the day number.
     * This is used to precisely locate a unique training entry for modification or detailed viewing.
     *
     * @param trainingId the ID of the training plan
     * @param exerciseId the ID of the exercise
     * @param dayOfExercise the day number the exercise is scheduled for
     * @return an Optional containing the unique Training entity
     */
    Optional<Training> findByTrainingIdAndExerciseIdAndDayOfExercise(int trainingId, int exerciseId, int dayOfExercise);

    /**
     * Retrieves all Training entries that use a specific exercise ID, regardless of the training plan.
     * This method is useful for finding all occurrences of a particular exercise across the entire system.
     *
     * @param exerciseId the ID of the exercise
     * @return a List of Training entities containing the specified exercise
     */
    List<Training> findAllByExerciseId(int exerciseId);
}