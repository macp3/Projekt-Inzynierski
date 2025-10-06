package com.example.demo.repositories;

import com.example.demo.entities.User;
import com.example.demo.entities.enums.Sex;
import com.example.demo.entities.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.image.BufferedImage;
import java.util.Date;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>
{
    void changePassword(int userId, String password);
    void changePrefferedDiet(int userId, String prefferedDiet);
    void changeProfilePicture(int userId, BufferedImage picture);
    void changeBodyParameters(int userId, Sex sex, float height, float weight, int age, int sportLevel, float goalWeight, float calorieLimit, float proteinLimit, float fatLimit, float carbohydratesLimit);

    ////////////////////////////////////////////////////

    void updateStreak(int userId, int streak);
    void updateStatus(int userId, Status status);
    void updatePremiumExpiration(int userId, Date date);

}
