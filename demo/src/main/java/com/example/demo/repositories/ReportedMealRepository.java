package com.example.demo.repositories;

import com.example.demo.entities.ReportedMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportedMealRepository extends JpaRepository<ReportedMeal, Integer>
{
    Optional<ReportedMeal> findByMealIdAndReportingId(int mealId, int reportingId);
    Optional<List<ReportedMeal>> findByMealId(int mealId);
    Optional<List<ReportedMeal>> findByReportingId(int reportingId);
}
