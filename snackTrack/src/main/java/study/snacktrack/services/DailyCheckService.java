package study.snacktrack.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import study.snacktrack.entities.BodyParameters;
import study.snacktrack.entities.RegisteredAlimentation;
import study.snacktrack.entities.User;
import study.snacktrack.repositories.BodyParametersRepository;
import study.snacktrack.repositories.RegisteredAlimentationRepository;
import study.snacktrack.repositories.UserRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Service responsible for daily calorie checks and updating user streaks.
 */
@Service
public class DailyCheckService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BodyParametersRepository bodyParametersRepository;

    @Autowired
    private RegisteredAlimentationRepository alimentationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FoodService foodService;

    /**
     * Scheduled task executed daily at midnight.
     * Checks calorie intake for all users and updates their streaks.
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Warsaw")
    public void checkCaloriesForAllUsers() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<User> users = userRepository.findAll();

        for (User user : users) {
            BodyParameters bodyParams = bodyParametersRepository.findByUserId(user.getId())
                    .orElse(null);

            if (bodyParams == null) {
                continue;
            }

            double calorieLimit = bodyParams.getCalorieLimit();
            double totalCalories = calculateDailyCalories(user.getId(), yesterday);

            if (totalCalories <= calorieLimit && totalCalories >= 0.7*calorieLimit) {
                userService.updateStreak(user.getId(), user.getStreak() + 1);
            } else {
                userService.updateStreak(user.getId(), 0);
            }
        }
    }

    /**
     * Calculates total calories consumed by a user on a given date.
     *
     * @param userId the ID of the user
     * @param date the date for which calories are calculated
     * @return total calories consumed
     */
    private double calculateDailyCalories(int userId, LocalDate date) {
        List<RegisteredAlimentation> entries = alimentationRepository.findByUserIdAndTimestamp(userId, date);

        return entries.stream()
                .mapToDouble(entry -> {
                    try {
                        if (entry.getEssentialFood() != null) {
                            if (entry.getAmount() != null) {
                                return entry.getEssentialFood().getCalories()
                                        * (entry.getAmount() / entry.getEssentialFood().getDefaultWeight());
                            } else if (entry.getPieces() != null) {
                                return entry.getEssentialFood().getCalories() * entry.getPieces();
                            }
                        } else if (entry.getMeal() != null) {
                            return entry.getMeal().getTotalCalories(foodService);
                        } else if (entry.getMealApiId() != null) {
                            var apiFood = foodService.getFoodFromApiById(entry.getMealApiId());

                            if (entry.getAmount() != null) {
                                return apiFood.getCalorie() * (entry.getAmount() / apiFood.getDefaultWeight());
                            } else if (entry.getPieces() != null) {
                                return apiFood.getCalorie() * entry.getPieces();
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Exception while counting user's calories: " + userId + ": " + e.getMessage());
                    }
                    return 0.0;
                })
                .sum();
    }
}
