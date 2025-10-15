package study.snacktrack.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import study.snacktrack.entities.MealDietType;

@Repository
public interface MealDietTypeRepository extends JpaRepository<MealDietType, Integer> {

    List<MealDietType> findByMealId(int mealId);

    void deleteByMealId(int mealId);
}
