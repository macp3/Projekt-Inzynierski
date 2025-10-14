package com.example.demo.repositories;

import com.example.demo.entities.Exercise;
import com.example.demo.entities.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Integer>
{
    List<Training> findAll();
    Optional<Training> findById(int trainingId);
    List<Training> findByTrainingId(int trainingId);
    boolean existsByTrainingIdAndExerciseIdAndDayOfExercise(int trainingId, int exerciseId, int dayOfExercise);
}
