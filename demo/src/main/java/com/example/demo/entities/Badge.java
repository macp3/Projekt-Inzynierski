package com.example.demo.entities;

import jakarta.persistence.*;

import java.awt.image.BufferedImage;

@Entity
@Table(name="badges")
public class Badge
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "user_id")
    private int userId;
    private String badge;
    @Column(name = "badge_logo")
    private BufferedImage badgeLogo;
}
