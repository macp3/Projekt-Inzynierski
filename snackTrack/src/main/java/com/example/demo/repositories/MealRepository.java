package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Meal;

@Repository
public interface MealRepository extends JpaRepository<Meal, Integer> {

    List<Meal> findByAuthorId(int authorId);

    List<Meal> findByNameContainingIgnoreCase(String name);

    @Query("SELECT m FROM Meal m LEFT JOIN FETCH m.ingredients WHERE m.id = :id")
    Meal findByIdWithIngredients(@Param("id") int id);
}
