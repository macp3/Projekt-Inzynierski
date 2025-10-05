package com.example.demo.entities;

import com.example.demo.entities.enums.Status;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.awt.image.BufferedImage;
import java.util.Date;

@Entity
@Table(name = "users")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private  String name;
    private String surname;
    private String email;
    private String login;
    private String password;
    //nad tym bedzie sie trzeba zastnowic jak zrobic image w sql - narazie zostawiam bez adnotacji
    @Nullable
    private BufferedImage profilePicture;
    @Column(name = "premium_expiration")
    @Nullable
    private Date premiumExpiration;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "preffered_diet")
    @Nullable
    private String prefferedDiet;
    private int streak;


}
