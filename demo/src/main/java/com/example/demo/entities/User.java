package com.example.demo.entities;

import com.example.demo.entities.enums.Status;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.Date;

@Entity
@Table(name = "users")
public class User
{
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    private  String name;
    @NotNull
    private String surname;
    @NotNull
    @Column(unique = true)
    private String email;
    @NotNull
    @Column(unique = true)
    private String login;
    @NotNull
    private String password;

    //nad tym bedzie sie trzeba zastnowic jak zrobic image w sql - narazie zostawiam bez adnotacji
    @Nullable
    private BufferedImage profilePicture;
    @Column(name = "premium_expiration")
    @Nullable
    private Date premiumExpiration;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "preffered_diet")
    @Nullable
    private String prefferedDiet;
    @NotNull
    private int streak;


}
