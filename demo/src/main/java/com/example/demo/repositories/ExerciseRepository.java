package com.example.demo.repositories;

import com.example.demo.entities.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer>
{
    boolean existsByName(String name);
    //List<Exercise> findAllByTrainingId(int trainingId);
}
