package study.snacktrack.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class CommentResponse {
    private int id;
    private int authorId;
    private String content;
    private int mealId;

    // Nowe pola
    private int likesCount;
    private boolean isLiked; // Czy zalogowany user to polubił?
}