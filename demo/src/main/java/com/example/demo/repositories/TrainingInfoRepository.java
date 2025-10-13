package com.example.demo.repositories;

import com.example.demo.entities.TrainingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingInfoRepository extends JpaRepository<TrainingInfo, Integer>
{

}
