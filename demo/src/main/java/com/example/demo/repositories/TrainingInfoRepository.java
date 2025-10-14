package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.TrainingInfo;

@Repository
public interface TrainingInfoRepository extends JpaRepository<TrainingInfo, Integer> {

    boolean existsByName(String name);
}
