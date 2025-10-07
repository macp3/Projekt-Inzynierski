package com.example.demo.services;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.entities.User;
import com.example.demo.entities.enums.Sex;
import com.example.demo.entities.enums.Status;
import com.example.demo.repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        userRepository.changePassword(userId, password);
    }

    //do naprawy w strukturze bazy danych (String -> enum)
    public void changePrefferedDiet(int userId, String prefferedDiet) {
    }

    //do rozbudowania:
    //po pierwsze pole daily_activity_factor na podstawie ktorego bedziemy obliczac limity
    //po drugie pole estimated_time zeby nie przekraczac deficytu/nadwyzki 1000kcal (bo to niezdrowe)
    //po trzecie mozliwosc edytowania ww. daily_activity_factor
    //po czwarte pole weight_change_tempo (0 - 1kg na tydzien) 0 = deficyt 0kcal | 1kg/tydzien = deficyt 100kcal itd. i to tez edytowalne
    //TAK DZIALA FITATU
    public void changeBodyParameters(int userId, Sex sex, float height, float weight, int age, int sportLevel, float goalWeight, float calorieLimit, float proteinLimit, float fatLimit, float carbohydratesLimit) {
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

        if (sportLevel > 3 || sportLevel < 1) {
            System.out.println("Your sport level must be 1, 2 or 3");
            return;
        }

        if (goalWeight < 0) {
            System.out.println("Your goal weight must be greater than zero");
            return;
        }

        if (calorieLimit < 0 || proteinLimit < 0 || fatLimit < 0 || carbohydratesLimit < 0) {
            System.out.println("Macronutrients must be greater than zero");
            return;
        }

        //tutaj oblicz zapotrzebowanie i limity makroskladnikow
        User user = optionalUser.get();
        userRepository.changeBodyParameters(userId, sex, height, weight, age, sportLevel, goalWeight, calorieLimit, proteinLimit, fatLimit, carbohydratesLimit);
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
        userRepository.updateStatus(userId, status);
    }

    public void updatePremiumExpiration(int userId, Date date) {
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
        userRepository.updatePremiumExpiration(userId, date);
    }
}
