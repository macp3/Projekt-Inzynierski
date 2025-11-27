package study.snacktrack.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import study.snacktrack.entities.RegisteredAlimentation;
import study.snacktrack.entities.enums.MealNames;

/**
 * Repository interface for managing RegisteredAlimentation entities, which track a user's logged food consumption.
 * This extends JpaRepository to provide standard database access and specialized querying capabilities for logged meals.
 */
@Repository
public interface RegisteredAlimentationRepository extends JpaRepository<RegisteredAlimentation, Integer> {

    /**
     * Retrieves all registered alimentation entries associated with a specific user ID.
     * This method is typically used to fetch a complete historical record of a user's food log.
     *
     * @param userId the ID of the user
     * @return a List of RegisteredAlimentation entities belonging to the user
     */
    List<RegisteredAlimentation> findByUserId(Integer userId);

    /**
     * Finds all registered alimentation entries for a specific user on a particular date.
     * This query is essential for calculating a user's nutritional intake for a single day.
     *
     * @param userId the ID of the user
     * @param timestamp the specific date to check
     * @return a List of RegisteredAlimentation entities logged on the given date
     */
    List<RegisteredAlimentation> findByUserIdAndTimestamp(int userId, LocalDate timestamp);

    /**
     * Retrieves a single RegisteredAlimentation entity by its unique identifier.
     * Note that the return type is Object here, which might indicate a non-standard usage or type casting requirement elsewhere.
     *
     * @param id the primary key identifier of the entry
     * @return the RegisteredAlimentation entity as an Object
     */
    Object findById(Long id);

    /**
     * Finds registered alimentation entries filtered by user ID, date, and the specific meal name (e.g., Breakfast, Dinner).
     * This method allows for precise retrieval of all items logged for a particular meal instance on a certain day.
     *
     * @param userId the ID of the user
     * @param timestamp the specific date to check
     * @param mealName the specific meal name (e.g., LUNCH)
     * @return a List of RegisteredAlimentation entities matching all three criteria
     */
    List<RegisteredAlimentation> findByUserIdAndTimestampAndMealName(
            Integer userId, LocalDate timestamp, MealNames mealName);

}