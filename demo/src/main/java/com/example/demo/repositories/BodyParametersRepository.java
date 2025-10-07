package com.example.demo.repositories;

import com.example.demo.entities.BodyParameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BodyParametersRepository extends JpaRepository<BodyParameters, Integer>
{

}
