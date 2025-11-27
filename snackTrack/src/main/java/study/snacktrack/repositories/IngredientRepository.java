package study.snacktrack.repositories;

import java.util.List;

import study.snacktrack.entities.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Ingredient entities.
 * This extends JpaRepository to handle standard persistence operations and custom queries specific to meal components.
 */
@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {

    /**
     * Retrieves a list of all ingredients that belong to a specified meal ID.
     * This method is essential for fetching the complete component list when processing a specific meal.
     *
     * @param mealId the ID of the meal
     * @return a List of Ingredient entities associated with the meal
     */
    List<Ingredient> findByMealId(int mealId);
}