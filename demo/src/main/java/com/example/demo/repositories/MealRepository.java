package com.example.demo.repositories;

import com.example.demo.entities.EssentialFood;
import com.example.demo.entities.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Integer>
{
    List<Meal> findByAuthorId(int authorId);
    List<Meal> findByNameContainingIgnoreCase(String name);
    @Query("SELECT m FROM Meal m LEFT JOIN FETCH m.ingredients WHERE m.id = :id")
    Meal findByIdWithIngredients(@Param("id") int id);
}
