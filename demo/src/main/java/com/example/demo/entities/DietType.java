package com.example.demo.entities;

import com.example.demo.entities.enums.DietTypes;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = "diet_types")
public class DietType
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private int id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DietTypes name;
}
