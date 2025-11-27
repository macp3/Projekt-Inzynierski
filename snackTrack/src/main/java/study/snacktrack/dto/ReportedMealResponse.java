package study.snacktrack.dto;

/**
 * Data Transfer Object (DTO) used for carrying the details of a reported meal back to the moderation panel or user.
 * This structure includes the unique ID of the report, the reporter's ID, the targeted meal's ID, and the reason for the report.
 */
public class ReportedMealResponse {

    private int id;
    private int reportingId;
    private int mealId;
    private String content;

    /**
     * Constructs a new ReportedMealResponse with all the identifying details of a filed report.
     * This constructor is used by the service layer to map a database entity into a transmissible response object.
     *
     * @param id The unique ID of the report record.
     * @param reportingId The ID of the user who filed the report.
     * @param mealId The ID of the meal being reported.
     * @param content The descriptive reason provided by the reporter.
     */
    public ReportedMealResponse(int id, int reportingId, int mealId, String content) {
        this.id = id;
        this.reportingId = reportingId;
        this.mealId = mealId;
        this.content = content;
    }

    /**
     * Getters and setters for the DTO fields.
     * These methods provide standard access and modification capabilities for the reported meal details.
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
    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}