package com.example.demo.entities;

import jakarta.persistence.*;

@Entity
@Table (name = "comments")
public class Comment
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;
    @Column (name = "author_id")
    private int authorId;
    private String content;
    @Column (name = "author_id")
    private int mealId;
}
