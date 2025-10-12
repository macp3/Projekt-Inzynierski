package com.example.demo.dto;

public class ReportedCommentResponse
{
    private int id;
    private int reportingId;
    private int commentId;
    private String content;

    public ReportedCommentResponse(int id, int reportingId, int commentId, String content) {
        this.id = id;
        this.reportingId = reportingId;
        this.commentId = commentId;
        this.content = content;
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
