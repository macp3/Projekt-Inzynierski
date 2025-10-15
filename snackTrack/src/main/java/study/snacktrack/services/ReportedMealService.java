package study.snacktrack.services;

import java.util.List;
import java.util.Optional;

import study.snacktrack.dto.ReportedMealResponse;
import study.snacktrack.entities.Meal;
import study.snacktrack.entities.ReportedMeal;
import study.snacktrack.repositories.UserRepository;
import org.springframework.stereotype.Service;

import study.snacktrack.entities.User;
import study.snacktrack.repositories.CommentRepository;
import study.snacktrack.repositories.MealRepository;
import study.snacktrack.repositories.ReportedMealRepository;

@Service
public class ReportedMealService {

    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final CommentRepository commentRepository;
    private final ReportedMealRepository reportedMealRepository;

    public ReportedMealService(UserRepository userRepository, MealRepository mealRepository, CommentRepository commentRepository, ReportedMealRepository reportedMealRepository) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.commentRepository = commentRepository;
        this.reportedMealRepository = reportedMealRepository;
    }

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

    public ReportedMealResponse reportMeal(int mealId, int userId, String content) {
        Meal meal = validateMealExistance(mealId);
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content must not be empty");
        }
        reportedMealRepository.findByMealIdAndReportingId(mealId, userId)
                .ifPresent(x -> {
                    throw new IllegalArgumentException("You have already reported this meal");
                }
                );

        ReportedMeal reportedMeal = new ReportedMeal();
        reportedMeal.setReportingId(user.getId());
        reportedMeal.setMealId(meal.getId());
        reportedMeal.setContent(content);

        reportedMealRepository.save(reportedMeal);
        return new ReportedMealResponse(reportedMeal.getId(), reportedMeal.getReportingId(), reportedMeal.getMealId(), reportedMeal.getContent());
    }

    //admin
    public List<ReportedMeal> getAllReportsByUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("There is no user with specified ID"));
        List<ReportedMeal> rm = reportedMealRepository.findByReportingId(userId)
                .orElseThrow(() -> new IllegalArgumentException("This user have not reported any meal yet"));
        return rm;
    }

    //admin
    public List<ReportedMeal> getAllReportsByMeal(int mealId) {
        List<ReportedMeal> rm = reportedMealRepository.findByMealId(mealId)
                .orElseThrow(() -> new IllegalArgumentException("This user have not reported any meal yet"));
        return rm;
    }
}
