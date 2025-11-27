package study.snacktrack.dto;

/**
 * Data Transfer Object (DTO) used for carrying the details of a reported comment back to the moderation panel or user.
 * This structure includes the unique ID of the report, the reporter's ID, the targeted comment's ID, and the reason for the report.
 */
public class ReportedCommentResponse {

    private int id;
    private int reportingId;
    private int commentId;
    private String content;

    /**
     * Constructs a new ReportedCommentResponse with all the identifying details of a filed report.
     * This constructor is used by the service layer to map a database entity into a transmissible response object.
     *
     * @param id The unique ID of the report record.
     * @param reportingId The ID of the user who filed the report.
     * @param commentId The ID of the comment being reported.
     * @param content The descriptive reason provided by the reporter.
     */
    public ReportedCommentResponse(int id, int reportingId, int commentId, String content) {
        this.id = id;
        this.reportingId = reportingId;
        this.commentId = commentId;
        this.content = content;
    }

    /**
     * Getters and setters for the DTO fields.
     * These methods provide standard access and modification capabilities for the reported comment details.
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

    public int getCommentId() {
        return commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}