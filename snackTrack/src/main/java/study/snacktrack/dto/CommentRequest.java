package study.snacktrack.dto;

/**
 * Data Transfer Object (DTO) used for carrying the necessary data to create or update a comment.
 * This object encapsulates the comment's text content and the identifier of the meal it relates to.
 */
public class CommentRequest {

    private String content;
    private int mealId;

    /**
     * Getters and setters for the DTO fields.
     * These methods provide standard access and modification capabilities for the comment's content and meal ID.
     */
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }
}