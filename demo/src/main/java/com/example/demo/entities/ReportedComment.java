package com.example.demo.entities;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = "reported_comments")
public class ReportedComment
{
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    @Column(name = "reporting_id")
    private int reportingId;
    @NotNull
    @Column(name = "comment_id")
    private int commentId;
    @NotNull
    private String content;
}
