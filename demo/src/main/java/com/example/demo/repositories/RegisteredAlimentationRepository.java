package com.example.demo.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.RegisteredAlimentation;

@Repository
public interface RegisteredAlimentationRepository extends JpaRepository<RegisteredAlimentation, Integer> {

    List<RegisteredAlimentation> findByUserId(Integer userId);

    List<RegisteredAlimentation> findByUserIdAndTimestamp(int userId, LocalDate timestamp);

    public Object findById(Long id);

}
