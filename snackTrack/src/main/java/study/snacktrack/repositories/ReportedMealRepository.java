package study.snacktrack.repositories;

import java.util.List;
import java.util.Optional;

import study.snacktrack.entities.ReportedMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing ReportedMeal entities, which track user reports against meals.
 * This extends JpaRepository to handle standard persistence operations and specialized lookups related to meal reports.
 */
@Repository
public interface ReportedMealRepository extends JpaRepository<ReportedMeal, Integer> {

    /**
     * Retrieves a specific report entry using both the meal ID and the ID of the user who filed the report.
     * This method is essential for ensuring that a single user cannot submit duplicate reports for the same meal.
     *
     * @param mealId the ID of the reported meal
     * @param reportingId the ID of the user who filed the report
     * @return an Optional containing the ReportedMeal entity if the specific report exists
     */
    Optional<ReportedMeal> findByMealIdAndReportingId(int mealId, int reportingId);

    /**
     * Retrieves all report entries that have been filed against a specific meal ID.
     * This is crucial for administrators who need to review the total number and details of reports concerning one meal.
     *
     * @param mealId the ID of the reported meal
     * @return an Optional containing a List of all ReportedMeal entities filed against the meal
     */
    Optional<List<ReportedMeal>> findByMealId(int mealId);

    /**
     * Retrieves all report entries that were filed by a specific user.
     * This method is useful for tracking a user's activity within the moderation system and managing their reporting history.
     *
     * @param reportingId the ID of the user who filed the reports
     * @return an Optional containing a List of ReportedMeal entities created by the user
     */
    Optional<List<ReportedMeal>> findByReportingId(int reportingId);
}