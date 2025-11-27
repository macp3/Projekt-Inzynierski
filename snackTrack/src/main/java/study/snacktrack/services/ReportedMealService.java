package study.snacktrack.services;

import java.util.List;
import java.util.Optional;

import study.snacktrack.dto.ReportedMealResponse;
import study.snacktrack.entities.Meal;
import study.snacktrack.entities.ReportedMeal;
import study.snacktrack.repositories.UserRepository;
import org.springframework.stereotype.Service;

import study.snacktrack.entities.User;
import study.snacktrack.repositories.MealRepository;
import study.snacktrack.repositories.ReportedMealRepository;

/**
 * Service responsible for managing reported meals.
 * Provides functionality for reporting, retrieving, and deleting meal reports.
 */
@Service
public class ReportedMealService {

    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final ReportedMealRepository reportedMealRepository;

    /**
     * Constructs ReportedMealService with required repositories.
     */
    public ReportedMealService(UserRepository userRepository, MealRepository mealRepository, ReportedMealRepository reportedMealRepository) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.reportedMealRepository = reportedMealRepository;
    }

    /**
     * Validates if a meal exists by ID.
     *
     * @param mealId meal identifier
     * @return Meal entity if found
     */
    private Meal validateMealExistance(int mealId) {
        if (mealId <= 0) {
            throw new IllegalArgumentException("Meal ID must be greater than zero");
        }

        Optional<Meal> optionalMeal = mealRepository.findById(mealId);

        if (optionalMeal.isEmpty()) {
            throw new IllegalArgumentException("Meal with specified ID doesn't exist");
        }

        return optionalMeal.get();
    }

    /**
     * Reports a meal by a user with provided content.
     *
     * @param mealId meal identifier
     * @param userId reporting user identifier
     * @param content report content
     * @return response DTO with report details
     */
    public ReportedMealResponse reportMeal(int mealId, int userId, String content) {
        Meal meal = validateMealExistance(mealId);
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (userId == meal.getAuthorId())
            throw new IllegalArgumentException("You cannot report your own meal");

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content must not be empty");
        }
        reportedMealRepository.findByMealIdAndReportingId(mealId, userId)
                .ifPresent(x -> {
                    throw new IllegalArgumentException("You have already reported this meal");
                });

        ReportedMeal reportedMeal = new ReportedMeal();
        reportedMeal.setReportingId(user.getId());
        reportedMeal.setMealId(meal.getId());
        reportedMeal.setContent(content);

        reportedMealRepository.save(reportedMeal);
        return new ReportedMealResponse(reportedMeal.getId(), reportedMeal.getReportingId(), reportedMeal.getMealId(),
                reportedMeal.getContent());
    }

    /**
     * Retrieves all reports created by a specific user.
     *
     * @param userId user identifier
     * @return list of reported meals
     */
    public List<ReportedMeal> getAllReportsByUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("There is no user with specified ID"));
        List<ReportedMeal> rm = reportedMealRepository.findByReportingId(userId)
                .orElseThrow(() -> new IllegalArgumentException("This user have not reported any meal yet"));
        return rm;
    }

    /**
     * Retrieves all reports for a specific meal.
     *
     * @param mealId meal identifier
     * @return list of reported meals
     */
    public List<ReportedMeal> getAllReportsByMeal(int mealId) {
        List<ReportedMeal> rm = reportedMealRepository.findByMealId(mealId)
                .orElseThrow(() -> new IllegalArgumentException("This user have not reported any meal yet"));
        return rm;
    }

    /**
     * Retrieves all reported meals in the system.
     *
     * @return list of reported meals
     */
    public List<ReportedMeal> getAllReports() {
        return reportedMealRepository.findAll();
    }

    /**
     * Deletes a reported meal by its report ID.
     *
     * @param reportId report identifier
     */
    public void deleteReport(int reportId) {
        if (reportId <= 0) {
            throw new IllegalArgumentException("Report ID must be greater than zero");
        }

        ReportedMeal report = reportedMealRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report with ID " + reportId + " not found"));

        reportedMealRepository.delete(report);
    }

    /**
     * Deletes all reports for a specific meal.
     * Useful when a meal is removed, although ON DELETE CASCADE in the database
     * may already handle this automatically.
     *
     * @param mealId meal identifier
     */
    public void deleteReportsByMealId(int mealId) {
        List<ReportedMeal> reports = reportedMealRepository.findByMealId(mealId).orElse(List.of());
        reportedMealRepository.deleteAll(reports);
    }
}
