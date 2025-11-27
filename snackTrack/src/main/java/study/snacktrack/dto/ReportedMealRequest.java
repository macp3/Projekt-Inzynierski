package study.snacktrack.dto;

/**
 * Data Transfer Object (DTO) used for carrying the information needed to file a report against an existing meal.
 * This structure contains the unique identifier of the meal being reported and the user-provided reason for the report.
 */
public class ReportedMealRequest {

    private int mealId;
    private String content;

    /**
     * Constructs a new ReportedMealRequest with the target meal ID and the reason for the report.
     * This constructor is utilized when a user submits a moderation request against a meal.
     *
     * @param mealId The ID of the meal being reported.
     * @param content The text describing the reason for the report.
     */
    public ReportedMealRequest(int mealId, String content) {
        this.mealId = mealId;
        this.content = content;
    }

    /**
     * Getters and setters for the DTO fields.
     * These methods provide standard access and modification capabilities for the reported meal details.
     */
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