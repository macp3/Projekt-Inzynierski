package study.snacktrack.repositories;

import java.util.List;

import study.snacktrack.entities.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Meal entities.
 * This extends JpaRepository to provide standard persistence operations and custom methods for searching meals.
 */
@Repository
public interface MealRepository extends JpaRepository<Meal, Integer> {

    /**
     * Retrieves all Meal entities created by a specific author ID.
     * This method is essential for users to access and manage their own list of created meals.
     *
     * @param authorId the ID of the meal creator (user)
     * @return a List of Meal entities authored by the specified user
     */
    List<Meal> findByAuthorId(int authorId);

    /**
     * Finds a list of meals whose names contain the specified string, performing a case-insensitive search.
     * This method is crucial for implementing robust search functionality, allowing users to discover meals quickly.
     *
     * @param name the substring to search for within meal names
     * @return a List of Meal entities matching the search criteria
     */
    List<Meal> findByNameContainingIgnoreCase(String name);
}