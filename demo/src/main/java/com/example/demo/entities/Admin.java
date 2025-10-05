package com.example.demo.entities;

import jakarta.persistence.*;

@Entity
@Table(name="admins")
public class Admin
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String login;
    @Column(unique = true)
    private String email;
    private String password;
}
