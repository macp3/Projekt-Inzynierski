package com.example.demo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Favourite;

public interface FavouriteRepository extends JpaRepository<Favourite, Integer> {

    Optional<Favourite> findByUserIdAndMealId(int userId, int mealId);

    List<Favourite> findByUserId(int userId);
}
