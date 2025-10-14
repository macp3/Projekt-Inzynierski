package com.example.demo.entities;

import org.jetbrains.annotations.NotNull;

import com.example.demo.entities.enums.DietTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "diet_types")
public class DietType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private int id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DietTypes name;
}
