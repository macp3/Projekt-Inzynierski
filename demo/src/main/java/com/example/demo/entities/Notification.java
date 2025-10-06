package com.example.demo.entities;

import com.example.demo.entities.enums.Recipients;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Entity
@Table(name = "notifications")
public class Notification
{
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    @Column(name = "author_id")
    private int authorId;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Recipients recipients;
    @NotNull
    @Column(name = "sending_time")
    private Date sendingTime;

}
