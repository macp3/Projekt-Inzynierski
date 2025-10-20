package study.snacktrack.repositories;

import java.util.List;
import java.util.Optional;

import study.snacktrack.entities.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MealRepository extends JpaRepository<Meal, Integer> {

    List<Meal> findByAuthorId(int authorId);

    List<Meal> findByNameContainingIgnoreCase(String name);

    @Query("SELECT m FROM Meal m LEFT JOIN FETCH m.ingredients WHERE m.id = :id")
    Meal findByIdWithIngredients(@Param("id") int id);
    boolean existsByName(String name);
}
