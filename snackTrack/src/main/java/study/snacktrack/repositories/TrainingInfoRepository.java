package study.snacktrack.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import study.snacktrack.entities.TrainingInfo;

/**
 * Repository interface for managing TrainingInfo entities, which store metadata about specific training plans.
 * This extends JpaRepository to provide standard persistence operations for training information.
 */
@Repository
public interface TrainingInfoRepository extends JpaRepository<TrainingInfo, Integer> {

    /**
     * Checks if a training plan already exists with the given name.
     * This is useful for initial creation validation to ensure that all training plans have unique identifiers.
     *
     * @param name the name of the training plan to check
     * @return true if a training plan with the name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Checks for the existence of a training plan with the specified name, excluding a specific ID.
     * This is primarily used during an update operation to ensure the new name is unique among all other existing records.
     *
     * @param name the name of the training plan to check
     * @param id the ID of the TrainingInfo entity to exclude from the search
     * @return true if another training plan with the same name exists, false otherwise
     */
    boolean existsByNameAndIdNot(String name, Integer id);
}