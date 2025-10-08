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

    public boolean changePassword(int userId, String password) {
        if (userId <= 0) {
            System.out.println("UserID must be greater than zero");
            return false;
        }

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            System.out.println("Couldn't find the user with provided ID");
            return false;
        }

        User user = optionalUser.get();
        if (password == null || user.getPassword().equals(password)) {
            System.out.println("New password cannot be the same as the current password");
            return false;
        }
        user.setPassword(password);
        userRepository.save(user);
        System.out.println("Password succesfully changed");
        return true;
    }

    //do naprawy w strukturze bazy danych (String -> enum)
    public void changePrefferedDiet(int userId, String prefferedDiet) {
    }

    //do rozbudowania:
    //po drugie pole estimated_time zeby nie przekraczac deficytu/nadwyzki 1000kcal (bo to niezdrowe)
    //TAK DZIALA FITATU
    public boolean changeBodyParameters(Integer userId, Sex sex, Float height, Float weight, Integer age, Float dailyActivityFactor, Float dailyActivityTrainingFactor, Float weeklyWeightChangeTempo, Float goalWeight) {
        if (userId <= 0) {
            System.out.println("UserID must be greater than zero");
            return false;
        }

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            System.out.println("Couldn't find the user with provided ID");
            return false;
        }
        else {
            User user = optionalUser.get();
            Optional<BodyParameters> optionalBodyParameters = bodyParametersRepository.findById(user.getId());
            if (optionalBodyParameters.isEmpty()) {
                System.out.println("Couldn't access user's body parameters");
                return false;
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

    public boolean updateStreak(int userId, int streak) {
        if (userId <= 0) {
            System.out.println("UserID must be greater than zero");
            return false;
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            System.out.println("Couldn't find the user with provided ID");
            return false;
        }
        User user = optionalUser.get();
        user.setStreak(streak);

        userRepository.save(user);
        return true;
    }

    public boolean updateStatus(int userId, Status status) {
        if (userId <= 0) {
            System.out.println("UserID must be greater than zero");
            return false;
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            System.out.println("Couldn't find the user with provided ID");
            return false;
        }

        if (status == null) {
            System.out.println("Specify the user's new status");
            return false;
        }

        User user = optionalUser.get();
        user.setStatus(status);

        userRepository.save(user);
        return true;
    }

    public boolean updatePremiumExpiration(int userId, LocalDate date) {
        if (userId <= 0) {
            System.out.println("UserID must be greater than zero");
            return false;
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            System.out.println("Couldn't find the user with provided ID");
            return false;
        }

        //zmien wszystkie Date na LocalDate w encjach - bedzie latwiej
        //przyrownaj premiumExpirationDate do teraz
        LocalDate now = LocalDate.now();
        User user = optionalUser.get();

        if (user.getPremiumExpiration() != null && user.getPremiumExpiration().isBefore(now)) {
            System.out.println("Specified premium expiration date is before now");
            return false;
        }
        user.setPremiumExpiration(date);

        userRepository.save(user);
        return true;
    }

    //delete user
}
