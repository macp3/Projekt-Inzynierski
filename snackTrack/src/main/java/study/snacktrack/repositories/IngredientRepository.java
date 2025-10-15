package study.snacktrack.repositories;

import java.util.List;

import study.snacktrack.entities.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {

    List<Ingredient> findByMealId(int mealId);
}
