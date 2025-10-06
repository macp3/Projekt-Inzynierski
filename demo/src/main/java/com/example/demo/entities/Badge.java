package com.example.demo.entities;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

@Entity
@Table(name="badges")
public class Badge
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private int id;
    @Column(name = "user_id")
    @NotNull
    private int userId;
    @NotNull
    private String badge;
    @Column(name = "badge_logo")
    @NotNull
    private BufferedImage badgeLogo;
}
