package com.example.demo.repositories;

import com.example.demo.entities.User;
import com.example.demo.entities.enums.Sex;
import com.example.demo.entities.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.image.BufferedImage;
import java.util.Date;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>
{
    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :userId")
    void changePassword(int userId, String password);

    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :userId")
    void changePrefferedDiet(int userId, String prefferedDiet);

    @Modifying
    @Query("UPDATE User u SET u.profilePicture = :picture WHERE u.id = :userId")
    void changeProfilePicture(int userId, BufferedImage picture);

    @Modifying
    @Query("""
    UPDATE User u 
    SET 
        u.sex = :sex,
        u.height = :height,
        u.weight = :weight,
        u.age = :age,
        u.sportLevel = :sportLevel,
        u.goalWeight = :goalWeight,
        u.calorieLimit = :calorieLimit,
        u.proteinLimit = :proteinLimit,
        u.fatLimit = :fatLimit,
        u.carbohydratesLimit = :carbohydratesLimit
    WHERE u.id = :userId
    """)
    void changeBodyParameters(int userId, Sex sex, float height, float weight, int age, int sportLevel, float goalWeight, float calorieLimit, float proteinLimit, float fatLimit, float carbohydratesLimit);

    ////////////////////////////////////////////////////

    @Modifying
    @Query("UPDATE User u SET u.streak = :streak WHERE u.id = :userId")
    void updateStreak(int userId, int streak);

    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :userId")
    void updateStatus(int userId, Status status);

    @Modifying
    @Query("UPDATE User u SET u.premiumExpiration = :date WHERE u.id = :userId")
    void updatePremiumExpiration(int userId, Date date);

}
