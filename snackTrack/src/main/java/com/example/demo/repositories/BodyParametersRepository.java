package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.BodyParameters;

@Repository
public interface BodyParametersRepository extends JpaRepository<BodyParameters, Integer> {

    Optional<BodyParameters> findByUserId(int userId);
}
