package study.snacktrack.repositories;

import java.util.List;
import java.util.Optional;

import study.snacktrack.entities.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavouriteRepository extends JpaRepository<Favourite, Integer> {

    Optional<Favourite> findByUserIdAndMealId(int userId, int mealId);

    List<Favourite> findByUserId(int userId);
}
