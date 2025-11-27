package study.snacktrack.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import study.snacktrack.entities.Exercise;

/**
 * Repository interface for managing Exercise entities.
 * This extends JpaRepository to handle standard CRUD operations for exercise definitions within the application.
 */
@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {

    /**
     * Checks if an exercise already exists with the given unique name.
     * This is useful for validating new exercise entries to prevent duplication in the database.
     *
     * @param name the name of the exercise to check
     * @return true if an exercise with the name exists, false otherwise
     */
    boolean existsByName(String name);
}