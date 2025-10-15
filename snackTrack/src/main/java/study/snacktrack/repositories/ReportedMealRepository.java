package study.snacktrack.repositories;

import java.util.List;
import java.util.Optional;

import study.snacktrack.entities.ReportedMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportedMealRepository extends JpaRepository<ReportedMeal, Integer> {

    Optional<ReportedMeal> findByMealIdAndReportingId(int mealId, int reportingId);

    Optional<List<ReportedMeal>> findByMealId(int mealId);

    Optional<List<ReportedMeal>> findByReportingId(int reportingId);
}
