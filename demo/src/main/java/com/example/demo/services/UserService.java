package com.example.demo.services;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.BodyParametersResponse;
import com.example.demo.entities.BodyParameters;
import com.example.demo.entities.User;
import com.example.demo.entities.enums.DietTypes;
import com.example.demo.entities.enums.Sex;
import com.example.demo.entities.enums.Status;
import com.example.demo.repositories.BodyParametersRepository;
import com.example.demo.repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BodyParametersRepository bodyParametersRepository;

    public UserService(UserRepository userRepository, BodyParametersRepository bodyParametersRepository) {
        this.userRepository = userRepository;
        this.bodyParametersRepository = bodyParametersRepository;
    }

    public User changePassword(int userId, String password) {
        User user = getUserById(userId);

        if (password == null || password.isBlank() || user.getPassword().equals(password)) {
            throw new IllegalArgumentException("New password cannot be empty or the same as the current password");
        }
        user.setPassword(password);
        userRepository.save(user);
        return user;
    }

    public User changePrefferedDiet(int userId, DietTypes prefferedDiet) {
        User user = getUserById(userId);

        boolean exists = Arrays.asList(DietTypes.values()).contains(prefferedDiet);
        if (prefferedDiet == null || !exists) {
            throw new IllegalArgumentException("There is no such diet type");
        }
        user.setPrefferedDiet(prefferedDiet);
        userRepository.save(user);
        return user;
    }

    //do rozbudowania:
    //po drugie pole estimated_time zeby nie przekraczac deficytu/nadwyzki 1000kcal (bo to niezdrowe)
    //TAK DZIALA FITATU
    public BodyParametersResponse changeBodyParameters(Integer userId, Sex sex, Float height, Float weight, Integer age, Float dailyActivityFactor, Float dailyActivityTrainingFactor, Float weeklyWeightChangeTempo, Float goalWeight) {
        User user = getUserById(userId);
        BodyParameters bodyParameters = bodyParametersRepository.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("This user has no body parameters specified"));

        if (sex == null) {
            sex = bodyParameters.getSex();
        }
        if (height <= 0) {
            height = bodyParameters.getHeight();
        }
        if (weight <= 0) {
            weight = bodyParameters.getWeight();
        }
        if (age <= 0) {
            age = bodyParameters.getAge();
        }
        if (goalWeight <= 0) {
            goalWeight = bodyParameters.getGoalWeight();
        }
        if (weeklyWeightChangeTempo < 0 || weeklyWeightChangeTempo > 1) {
            weeklyWeightChangeTempo = bodyParameters.getWeeklyWeightChangeTempo();
        }

        float formula = 0;
        if (sex == Sex.male) {
            formula = (float) ((10 * weight) + (6.25 * height) - (5 * age) + 5);
        } else if (sex == Sex.female) {
            formula = (float) ((10 * weight) + (6.25 * height) - (5 * age) - 161);
        }

        float caloricZero = 0;
        if (dailyActivityFactor < 0.65 || dailyActivityFactor > 1.2) {
            dailyActivityFactor = bodyParameters.getDailyActivityFactor();
        }
        if (dailyActivityTrainingFactor < 0.65 || dailyActivityTrainingFactor > 1.2) {
            dailyActivityTrainingFactor = bodyParameters.getDailyActivityTrainingFactor();
        }

        caloricZero = formula * (dailyActivityFactor + dailyActivityTrainingFactor);

        float calorieLimit = caloricZero;
        float proteinLimit = 0;
        float fatLimit = 0;
        float carbohydratesLimit = 0;
        if (goalWeight > weight) {
            calorieLimit = caloricZero + weeklyWeightChangeTempo * 1000;
            proteinLimit = (float) 0.2 * calorieLimit;
            fatLimit = (float) 0.25 * calorieLimit;
            carbohydratesLimit = (float) 0.55 * calorieLimit;
        } else if (goalWeight < weight) {
            calorieLimit = caloricZero - weeklyWeightChangeTempo * 1000;
            proteinLimit = (float) 0.25 * calorieLimit;
            fatLimit = (float) 0.2 * calorieLimit;
            carbohydratesLimit = (float) 0.55 * calorieLimit;
        } else {
            calorieLimit = caloricZero;
            proteinLimit = (float) 0.2 * calorieLimit;
            fatLimit = (float) 0.25 * calorieLimit;
            carbohydratesLimit = (float) 0.55 * calorieLimit;
        }

        bodyParameters.setSex(sex);
        bodyParameters.setHeight(height);
        bodyParameters.setWeight(weight);
        bodyParameters.setAge(age);
        bodyParameters.setDailyActivityFactor(dailyActivityFactor);
        bodyParameters.setDailyActivityTrainingFactor(dailyActivityTrainingFactor);
        bodyParameters.setWeeklyWeightChangeTempo(weeklyWeightChangeTempo);
        bodyParameters.setGoalWeight(goalWeight);
        bodyParameters.setCalorieLimit(calorieLimit);
        bodyParameters.setProteinLimit(proteinLimit);
        bodyParameters.setFatLimit(fatLimit);
        bodyParameters.setCarbohydratesLimit(carbohydratesLimit);

        bodyParametersRepository.save(bodyParameters);

        return new BodyParametersResponse(user.getId(), bodyParameters.getSex(), bodyParameters.getHeight(), bodyParameters.getWeight(), bodyParameters.getAge(), bodyParameters.getDailyActivityFactor(), bodyParameters.getDailyActivityTrainingFactor(), bodyParameters.getWeeklyWeightChangeTempo(), bodyParameters.getGoalWeight(), bodyParameters.getCalorieLimit(), bodyParameters.getProteinLimit(), bodyParameters.getFatLimit(), bodyParameters.getCarbohydratesLimit());
    }

    //admin
    public boolean updateStreak(int userId, int streak) {

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        user.setStreak(streak);
        userRepository.save(user);
        return true;
    }

    //admin
    public User updateStatus(int userId, Status status) {
        User user = getUserById(userId);
        user.setStatus(status);

        userRepository.save(user);
        return user;
    }

    //admin
    public User updatePremiumExpiration(int userId, LocalDate date) {
        User user = getUserById(userId);

        LocalDate now = LocalDate.now();

        if (user.getPremiumExpiration() != null && user.getPremiumExpiration().isBefore(now)) {
            throw new IllegalArgumentException("Specified premium expiration date is before now");
        }

        user.setPremiumExpiration(date);
        userRepository.save(user);
        return user;
    }

    //admin
    public void deleteUser(int userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
        System.out.println("User deleted successfully");
    }

    public User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public BodyParameters getUserBodyParameters(int userId) {
        User user = getUserById(userId);
        return bodyParametersRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("There is no body parameters for this user"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

}
