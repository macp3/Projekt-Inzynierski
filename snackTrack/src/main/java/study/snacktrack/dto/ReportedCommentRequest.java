package study.snacktrack.dto;

public class ReportedCommentRequest {

    private int commentId;
    private String content;

    public ReportedCommentRequest(int commentId, String content) {
        this.commentId = commentId;
        this.content = content;
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
