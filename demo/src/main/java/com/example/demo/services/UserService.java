package com.example.demo.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.entities.BodyParameters;
import com.example.demo.entities.User;
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

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    public void changePassword(int userId, String password) {
        if (userId <= 0) {
            System.out.println("UserID must be greater than zero");
            return;
        }

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            System.out.println("Couldn't find the user with provided ID");
            return;
        }

        User user = optionalUser.get();
        if (password == null || user.getPassword().equals(password)) {
            System.out.println("New password cannot be the same as the current password");
            return;
        }
        user.setPassword(password);
        userRepository.save(user);
        System.out.println("Password succesfully changed");
    }

    //do naprawy w strukturze bazy danych (String -> enum)
    public void changePrefferedDiet(int userId, String prefferedDiet) {
    }

    //do rozbudowania:
    //po drugie pole estimated_time zeby nie przekraczac deficytu/nadwyzki 1000kcal (bo to niezdrowe)
    //TAK DZIALA FITATU
    public void changeBodyParameters(int userId, Sex sex, float height, float weight, int age, float dailyActivityFactor, float dailyActivityTrainingFactor, float weeklyWeightChangeTempo, float goalWeight) {
        if (userId <= 0) {
            System.out.println("UserID must be greater than zero");
            return;
        }

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            System.out.println("Couldn't find the user with provided ID");
            return;
        }

        if (sex == null) {
            System.out.println("Unknown sex");
            return;
        }

        if (height < 0 || weight < 0 || age < 0) {
            System.out.println("Height, weight and age must be greater than zero");
            return;
        }

        if (goalWeight < 0) {
            System.out.println("Your goal weight must be greater than zero");
            return;
        }

        if (weeklyWeightChangeTempo < 0 || weeklyWeightChangeTempo > 1) {
            System.out.println("The maximum weight you can lose/gain per week is 1kg");
            return;
        }

        User user = optionalUser.get();

        float formula = 0;
        if (sex == Sex.male) {
            formula = (float) ((10 * weight) + (6.25 * height) - (5 * age) + 5);
        } else if (sex == Sex.female) {
            formula = (float) ((10 * weight) + (6.25 * height) - (5 * age) - 161);
        }

        float caloricZero = 0;
        if ((dailyActivityFactor < 0.65 || dailyActivityFactor > 1.2) || (dailyActivityTrainingFactor < 0.65)) {
            caloricZero = formula * (dailyActivityFactor + dailyActivityTrainingFactor);
        }

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

        Optional<BodyParameters> optionalBodyParameters = bodyParametersRepository.findById(user.getId());
        if (optionalBodyParameters.isEmpty()) {
            System.out.println("Couldn't access user's body parameters");
            return;
        }

        BodyParameters bodyParameters = optionalBodyParameters.get();
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
    }

    public void updateStreak(int userId, int streak) {
        if (userId <= 0) {
            System.out.println("UserID must be greater than zero");
            return;
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            System.out.println("Couldn't find the user with provided ID");
            return;
        }
        User user = optionalUser.get();
        user.setStreak(streak);

        userRepository.save(user);
    }

    public void updateStatus(int userId, Status status) {
        if (userId <= 0) {
            System.out.println("UserID must be greater than zero");
            return;
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            System.out.println("Couldn't find the user with provided ID");
            return;
        }

        if (status == null) {
            System.out.println("Specify the user's new status");
            return;
        }

        User user = optionalUser.get();
        user.setStatus(status);

        userRepository.save(user);
    }

    public void updatePremiumExpiration(int userId, LocalDate date) {
        if (userId <= 0) {
            System.out.println("UserID must be greater than zero");
            return;
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            System.out.println("Couldn't find the user with provided ID");
            return;
        }

        //zmien wszystkie Date na LocalDate w encjach - bedzie latwiej
        //przyrownaj premiumExpirationDate do teraz
        LocalDate now = LocalDate.now();

        User user = optionalUser.get();
        user.setPremiumExpiration(date);

        userRepository.save(user);
    }
}
