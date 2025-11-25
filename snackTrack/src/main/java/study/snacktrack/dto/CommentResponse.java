package study.snacktrack.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private int id;
    private int authorId;
    private String authorName;

    // 🔹 NOWE POLE
    private String authorImageUrl;

    private String content;
    private int mealId;
    private int likesCount;

    @JsonProperty("isLiked")
    private boolean isLiked;
}