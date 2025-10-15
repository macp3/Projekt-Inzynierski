package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.UserTraining;

@Repository
public interface UserTrainingRepository extends JpaRepository<UserTraining, Integer> {

    List<UserTraining> findByUserId(int userId);
    //UserTraining findByUserId(int userId);
    void deleteAllByTrainingId(int trainingId);
}
