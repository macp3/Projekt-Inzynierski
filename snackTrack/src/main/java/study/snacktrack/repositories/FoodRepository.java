package study.snacktrack.repositories;

import java.util.List;

import study.snacktrack.entities.EssentialFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<EssentialFood, Integer> {

    List<EssentialFood> findByNameContainingIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
