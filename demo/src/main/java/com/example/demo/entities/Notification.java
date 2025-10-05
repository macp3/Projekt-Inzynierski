package com.example.demo.entities;

import com.example.demo.entities.enums.Recipients;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "notifications")
public class Notification
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "author_id")
    private int authorId;
    private String name;
    private String description;
    private Recipients recipients;
    @Column(name = "sending_time")
    private Date sendingTime;

}
