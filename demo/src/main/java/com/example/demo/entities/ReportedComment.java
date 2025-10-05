package com.example.demo.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "reported_comments")
public class ReportedComment
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "reporting_id")
    private int reportingId;
    @Column(name = "comment_id")
    private int commentId;
    private String content;
}
