package com.example.demo.repositories;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.User;
import com.example.demo.entities.enums.Sex;
import com.example.demo.entities.enums.Status;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);
    boolean existsById(int id);

    Optional<User> findByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :userId")
    void changePassword(int userId, String password);

    @Modifying
    @Query("UPDATE User u SET u.prefferedDiet = :preferredDiet WHERE u.id = :userId")
    void changePrefferedDiet(int userId, String prefferedDiet);

    @Modifying
    @Query("UPDATE User u SET u.profilePicture = :picture WHERE u.id = :userId")
    void changeProfilePicture(int userId, BufferedImage picture);

    @Modifying
    @Query("""
    UPDATE BodyParameters b 
    SET 
        b.sex = :sex,
        b.height = :height,
        b.weight = :weight,
        b.age = :age,
        b.sportLevel = :sportLevel,
        b.goalWeight = :goalWeight,
        b.calorieLimit = :calorieLimit,
        b.proteinLimit = :proteinLimit,
        b.fatLimit = :fatLimit,
        b.carbohydratesLimit = :carbohydratesLimit
    WHERE b.id = :userId
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
