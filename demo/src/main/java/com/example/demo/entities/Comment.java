package com.example.demo.entities;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

@Entity
@Table (name = "comments")
public class Comment
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @NotNull
    private int id;
    @NotNull
    @Column (name = "author_id")
    private int authorId;
    @NotNull
    private String content;
    @NotNull
    @Column (name = "author_id")
    private int mealId;
}
