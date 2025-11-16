package study.snacktrack.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import study.snacktrack.entities.RegisteredAlimentation;
import study.snacktrack.entities.enums.MealNames;

@Repository
public interface RegisteredAlimentationRepository extends JpaRepository<RegisteredAlimentation, Integer> {

    List<RegisteredAlimentation> findByUserId(Integer userId);

    List<RegisteredAlimentation> findByUserIdAndTimestamp(int userId, LocalDate timestamp);

    public Object findById(Long id);

    List<RegisteredAlimentation> findByUserIdAndTimestampAndMealName(
            Integer userId, LocalDate timestamp, MealNames mealName);


}
