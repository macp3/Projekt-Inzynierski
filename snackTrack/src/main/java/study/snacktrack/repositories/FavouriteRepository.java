package study.snacktrack.repositories;

import java.util.List;
import java.util.Optional;

import study.snacktrack.entities.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Favourite entities, representing a user's saved favorite meals.
 * This extends JpaRepository to provide standard database access along with methods for specific favorite lookups.
 */
public interface FavouriteRepository extends JpaRepository<Favourite, Integer> {

    /**
     * Retrieves a specific Favourite record using both the user ID and the meal ID.
     * This method is essential for checking if a user has already favorited a particular meal before allowing them to add it again.
     *
     * @param userId the ID of the user
     * @param mealId the ID of the meal
     * @return an Optional containing the Favourite entity if the relationship exists
     */
    Optional<Favourite> findByUserIdAndMealId(int userId, int mealId);

    /**
     * Retrieves all favorite meal records associated with a given user ID.
     * This method is used to fetch the complete list of meals a user has marked as their favorites.
     *
     * @param userId the ID of the user
     * @return a List of Favourite entities belonging to the user
     */
    List<Favourite> findByUserId(int userId);
}