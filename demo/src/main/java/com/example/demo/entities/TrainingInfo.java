package com.example.demo.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "trainings_info")
public class TrainingInfo
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
}
