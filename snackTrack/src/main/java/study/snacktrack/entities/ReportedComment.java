package study.snacktrack.entities;

import org.jetbrains.annotations.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a user-submitted report against a specific comment within the application.
 * This entity is used by the moderation system to track which comment was reported, by whom, and the reason provided for the report.
 */
@Entity
@Table(name = "reported_comments")
public class ReportedComment {

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

    /**
     * Getters and setters for all entity fields.
     * These methods provide standard access and modification capabilities for the ReportedComment entity properties.
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReportingId() {
        return reportingId;
    }

    public void setReportingId(int reportingId) {
        this.reportingId = reportingId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    @NotNull
    public String getContent() {
        return content;
    }

    public void setContent(@NotNull String content) {
        this.content = content;
    }
}