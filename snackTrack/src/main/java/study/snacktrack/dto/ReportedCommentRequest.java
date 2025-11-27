package study.snacktrack.dto;

/**
 * Data Transfer Object (DTO) used for carrying the information needed to file a
 * report against an existing comment.
 * This structure contains the unique identifier of the comment being reported
 * and the user-provided reason for the report.
 */
public class ReportedCommentRequest {

    private int commentId;
    private String content;

    /**
     * Constructs a new ReportedCommentRequest with the target comment ID and the
     * reason for the report.
     * This constructor is utilized when a user submits a moderation request against
     * a comment.
     *
     * @param commentId The ID of the comment being reported.
     * @param content   The text describing the reason for the report.
     */
    public ReportedCommentRequest(int commentId, String content) {
        this.commentId = commentId;
        this.content = content;
    }

    /**
     * Getters and setters for the DTO fields.
     * These methods provide standard access and modification capabilities for the
     * reported comment details.
     */
    public int getCommentId() {
        return commentId;
    }

    public String getContent() {
        return content;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public void setContent(String content) {
        this.content = content;
    }
}