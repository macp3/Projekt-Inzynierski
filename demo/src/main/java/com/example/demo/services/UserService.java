package com.example.demo.services;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.example.demo.entities.DietType;
import com.example.demo.entities.enums.DietTypes;
import com.example.demo.security.JwtAuthFilter;
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

    private User validateUserExistance(int userId)
    {
        if (userId <= 0) {
            throw new IllegalArgumentException("UserID must be greater than zero");
        }

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Couldn't find the user with provided ID");
        }
        return optionalUser.get();
    }

    public UserService(UserRepository userRepository, BodyParametersRepository bodyParametersRepository) {
        this.userRepository = userRepository;
        this.bodyParametersRepository = bodyParametersRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean changePassword(int userId, String password) {
        User user = validateUserExistance(userId);
        if(user == null)
            throw new IllegalArgumentException("User not found");

        if (password == null || user.getPassword().equals(password)) {
            throw new IllegalArgumentException("New password cannot be the same as the current password");
        }
        user.setPassword(password);
        userRepository.save(user);
        System.out.println("Password succesfully changed");
        return true;
    }

    public boolean changePrefferedDiet(int userId, DietTypes prefferedDiet)
    {
        User user = validateUserExistance(userId);
        if(user == null)
            throw new IllegalArgumentException("User not found");

        boolean exists = Arrays.asList(DietTypes.values()).contains(prefferedDiet);
        if(prefferedDiet == null || !exists)
        {
            throw new IllegalArgumentException("There is no such diet type");
        }
        user.setPrefferedDiet(prefferedDiet);
        return true;
    }

    //do rozbudowania:
    //po drugie pole estimated_time zeby nie przekraczac deficytu/nadwyzki 1000kcal (bo to niezdrowe)
    //TAK DZIALA FITATU
    public boolean changeBodyParameters(Integer userId, Sex sex, Float height, Float weight, Integer age, Float dailyActivityFactor, Float dailyActivityTrainingFactor, Float weeklyWeightChangeTempo, Float goalWeight)
    {
        User user = validateUserExistance(userId);
        if(user == null)
            return false;
        else {
            Optional<BodyParameters> optionalBodyParameters = bodyParametersRepository.findById(user.getId());
            if (optionalBodyParameters.isEmpty()) {
                throw new IllegalArgumentException("Couldn't access user's body parameters");
            }
            else {
                BodyParameters userBodyParameters = optionalBodyParameters.get();


                if (sex == null) {
                    sex = userBodyParameters.getSex();
                }

                if (height <= 0 || weight <= 0 || age <= 0) {
                    height = userBodyParameters.getHeight();
                    weight = userBodyParameters.getWeight();
                    age = userBodyParameters.getAge();
                }

                if (goalWeight <= 0) {
                    goalWeight = userBodyParameters.getGoalWeight();
                }

                if (weeklyWeightChangeTempo < 0 || weeklyWeightChangeTempo > 1) {
                    weeklyWeightChangeTempo = userBodyParameters.getWeeklyWeightChangeTempo();
                }

                float formula = 0;
                if (sex == Sex.male) {
                    formula = (float) ((10 * weight) + (6.25 * height) - (5 * age) + 5);
                } else if (sex == Sex.female) {
                    formula = (float) ((10 * weight) + (6.25 * height) - (5 * age) - 161);
                }

                float caloricZero = 0;
                if ((dailyActivityFactor < 0.65 || dailyActivityFactor > 1.2) || (dailyActivityTrainingFactor < 0.65 || dailyActivityTrainingFactor > 1.2)) {
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
        }
        return true;
    }

    //admin
    public boolean updateStreak(int userId, int streak)
    {
        
        User user = validateUserExistance(userId);
        if(user == null)
            throw new IllegalArgumentException("User not found");

        user.setStreak(streak);
        userRepository.save(user);
        return true;
    }

    //admin
    public boolean updateStatus(int userId, Status status) {
        User user = validateUserExistance(userId);
        if(user == null)
            throw new IllegalArgumentException("User not found");
        user.setStatus(status);

        userRepository.save(user);
        return true;
    }

    //admin
    public boolean updatePremiumExpiration(int userId, LocalDate date) {
        User user = validateUserExistance(userId);
        if(user == null)
            throw new IllegalArgumentException("User not found");

        LocalDate now = LocalDate.now();

        if (user.getPremiumExpiration() != null && user.getPremiumExpiration().isBefore(now)) {
            throw new IllegalArgumentException("Specified premium expiration date is before now");
        }
        user.setPremiumExpiration(date);

        userRepository.save(user);
        return true;
    }

    //admin
    public boolean deleteUser(int userId)
    {
        User user = validateUserExistance(userId);
        if(user == null)
            throw new IllegalArgumentException("User not found");
        userRepository.delete(user);
        return true;
    }
}
