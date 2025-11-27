package study.snacktrack.repositories;

import java.util.List;

import study.snacktrack.entities.EssentialFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing EssentialFood entities, which represent common food items.
 * This extends JpaRepository to provide standard database access and custom query methods for food data.
 */
@Repository
public interface FoodRepository extends JpaRepository<EssentialFood, Integer> {

    /**
     * Finds a list of EssentialFood entities whose names contain the specified string, ignoring case.
     * This method is crucial for implementing search functionality, allowing users to find food items by typing partial names.
     *
     * @param name the substring to search for within food names
     * @return a List of EssentialFood entities matching the search criteria
     */
    List<EssentialFood> findByNameContainingIgnoreCase(String name);

    /**
     * Checks if an EssentialFood entity already exists with the given name, ignoring case differences.
     * This is useful for validating new food entries to ensure that each stored food item has a unique name.
     *
     * @param name the name of the food item to check
     * @return true if an EssentialFood with the given name exists, false otherwise
     */
    boolean existsByNameIgnoreCase(String name);
}