package com.example.demo.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "meals")
public class Meal
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "author_id")
    private int authorId;
    private String description;
}
