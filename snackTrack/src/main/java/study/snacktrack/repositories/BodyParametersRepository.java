package study.snacktrack.repositories;

import java.util.Optional;

import study.snacktrack.entities.BodyParameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing BodyParameters entities.
 * This repository handles persistence and retrieval operations specifically for user body metrics and calculation results.
 */
@Repository
public interface BodyParametersRepository extends JpaRepository<BodyParameters, Integer> {

    /**
     * Retrieves the body parameters associated with a specific user ID.
     * This method is crucial for accessing a user's health metrics to perform calculations.
     *
     * @param userId the ID of the user
     * @return an Optional containing the BodyParameters entity or empty if not found
     */
    Optional<BodyParameters> findByUserId(int userId);

    /**
     * Checks if a BodyParameters record already exists for a given user ID.
     * This method is typically used to determine whether to create a new record or update an existing one.
     *
     * @param userId the ID of the user to check
     * @return true if a BodyParameters record exists, false otherwise
     */
    boolean existsByUserId(Integer userId);
}