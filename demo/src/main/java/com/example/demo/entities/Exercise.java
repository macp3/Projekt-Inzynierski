package com.example.demo.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "exercises")
public class Exercise
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String description;
    private String type;
    private int difficulty;
    @Column(name = "number_of_sets")
    private int numberOfSets;
    private int intensivity;
}
