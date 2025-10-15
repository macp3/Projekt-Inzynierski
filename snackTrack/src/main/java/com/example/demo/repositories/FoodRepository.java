package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.EssentialFood;

@Repository
public interface FoodRepository extends JpaRepository<EssentialFood, Integer> {

    List<EssentialFood> findByNameContainingIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
