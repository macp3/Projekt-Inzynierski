package com.example.demo.repositories;

import com.example.demo.entities.ReportedMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportedMealRepository extends JpaRepository<ReportedMeal, Integer>
{

}
