package study.snacktrack.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import study.snacktrack.dto.CommentRequest;
import study.snacktrack.dto.CommentResponse;
import study.snacktrack.entities.Comment;
import study.snacktrack.entities.CommentLike;
import study.snacktrack.entities.Meal;
import study.snacktrack.entities.User;
import study.snacktrack.repositories.CommentLikeRepository;
import study.snacktrack.repositories.CommentRepository;
import study.snacktrack.repositories.MealRepository;
import study.snacktrack.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for handling comment-related business logic,
 * including creation, editing, deletion, and like management.
 */
@Service
public class CommentService {

    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    public CommentService(UserRepository userRepository, MealRepository mealRepository,
                          CommentRepository commentRepository, CommentLikeRepository commentLikeRepository) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
    }

    /**
     * Formats the user's name as "Name Surname".
     */
    private String formatAuthorName(User user) {
        return user.getName() + " " + user.getSurname();
    }

    /**
     * Retrieves a User entity by ID or returns null if not found.
     */
    private User getUserById(int userId) {
        return userRepository.findById(userId).orElse(null);
    }

    /**
     * Validates if a comment exists for the given ID.
     */
    private Comment validateCommentExistance(int commentId) {
        if (commentId <= 0) {
            throw new IllegalArgumentException("Invalid ID");
        }
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
    }

    /**
     * Adds a new comment to a specified meal by an authenticated author.
     *
     * @param authorId The ID of the user creating the comment.
     * @param request The CommentRequest DTO.
     * @return The created CommentResponse DTO.
     * @throws IllegalArgumentException If user/meal is not found, content is empty, or comment already exists.
     */
    public CommentResponse addCommentToMeal(int authorId, CommentRequest request) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Meal meal = mealRepository.findById(request.getMealId())
                .orElseThrow(() -> new IllegalArgumentException("Meal not found"));

        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("Content must not be empty");
        }

        if (commentRepository.existsByMealIdAndAuthorId(meal.getId(), author.getId())) {
            throw new IllegalArgumentException("You have already commented this meal");
        }

        Comment comment = new Comment();
        comment.setAuthorId(author.getId());
        comment.setMealId(meal.getId());
        comment.setContent(request.getContent());
        commentRepository.save(comment);

        return new CommentResponse(
                comment.getId(),
                author.getId(),
                formatAuthorName(author),
                author.getImageUrl(),
                comment.getContent(),
                meal.getId(),
                0,
                false);
    }

    /**
     * Edits the content of an existing comment, verifying ownership.
     *
     * @param authorId The ID of the user attempting to edit.
     * @param commentId The ID of the comment to edit.
     * @param content The new content for the comment.
     * @return The updated CommentResponse DTO.
     * @throws IllegalArgumentException If user/comment is not found, content is empty, or not authorized.
     */
    public CommentResponse editComment(int authorId, int commentId, String content) {
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Comment comment = validateCommentExistance(commentId);

        if (user.getId() != comment.getAuthorId()) {
            throw new IllegalArgumentException("Not authorized");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content empty");
        }

        comment.setContent(content);
        commentRepository.save(comment);

        boolean isLiked = commentLikeRepository.existsByUserIdAndCommentId(authorId, commentId);

        return new CommentResponse(
                comment.getId(),
                authorId,
                formatAuthorName(user),
                user.getImageUrl(),
                comment.getContent(),
                comment.getMealId(),
                comment.getLikes(),
                isLiked);
    }

    /**
     * Deletes a comment created by a specific user for a specific meal.
     *
     * @param authorId The ID of the user attempting to delete.
     * @param mealId The ID of the meal.
     * @throws IllegalArgumentException If comment is not found or not authorized.
     */
    public void deleteCommentByUser(int authorId, int mealId) {
        Comment comment = commentRepository.findByMealIdAndAuthorId(mealId, authorId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (comment.getAuthorId() != authorId) {
            throw new IllegalArgumentException("Not authorized");
        }
        commentRepository.delete(comment);
    }

    /**
     * Retrieves all comments created by a specific user.
     * Includes the `isLiked` status relative to the current user.
     *
     * @param userId The ID of the user whose comments are requested.
     * @return List of CommentResponse DTOs.
     * @throws IllegalArgumentException If user is not found.
     */
    public List<CommentResponse> getAllCommentsByUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String userName = formatAuthorName(user);
        String userImage = user.getImageUrl();

        return commentRepository.findByAuthorId(userId).stream()
                .map(c -> {
                    boolean isLiked = commentLikeRepository.existsByUserIdAndCommentId(userId, c.getId());
                    return new CommentResponse(
                            c.getId(),
                            c.getAuthorId(),
                            userName,
                            userImage,
                            c.getContent(),
                            c.getMealId(),
                            c.getLikes(),
                            isLiked);
                })
                .toList();
    }

    /**
     * Retrieves a single comment made by a specific user for a specific meal.
     *
     * @param mealId The ID of the meal.
     * @param userId The ID of the user.
     * @return The Comment entity.
     * @throws IllegalArgumentException If no comment is found.
     */
    public Comment getUserMealComment(int mealId, int userId) {
        return commentRepository.findByMealIdAndAuthorId(mealId, userId)
                .orElseThrow(() -> new IllegalArgumentException("No comment found"));
    }

    /**
     * Retrieves all comments for a specific meal.
     * Includes the `isLiked` status relative to the `currentUserId`.
     *
     * @param mealId The ID of the meal.
     * @param currentUserId The ID of the authenticated user viewing the comments.
     * @return List of CommentResponse DTOs.
     * @throws IllegalArgumentException If meal is not found.
     */
    public List<CommentResponse> getAllMealComments(int mealId, int currentUserId) {
        if (!mealRepository.existsById(mealId)) {
            throw new IllegalArgumentException("Meal not found");
        }

        List<Comment> comments = commentRepository.findByMealId(mealId);
        List<CommentResponse> response = new ArrayList<>();

        for (Comment c : comments) {
            boolean isLiked = commentLikeRepository.existsByUserIdAndCommentId(currentUserId, c.getId());

            User author = getUserById(c.getAuthorId());
            String name = (author != null) ? formatAuthorName(author) : "Unknown";
            String image = (author != null) ? author.getImageUrl() : null;

            response.add(new CommentResponse(
                    c.getId(),
                    c.getAuthorId(),
                    name,
                    image,
                    c.getContent(),
                    c.getMealId(),
                    c.getLikes(),
                    isLiked));
        }
        return response;
    }

    /**
     * Toggles the like status of a comment for a given user.
     * Updates the `likes` count on the comment entity.
     *
     * @param userId The ID of the user liking/unliking the comment.
     * @param commentId The ID of the comment.
     * @throws IllegalArgumentException If comment is not found.
     */
    @Transactional
    public void toggleLike(int userId, int commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        Optional<CommentLike> existingLike = commentLikeRepository.findByUserIdAndCommentId(userId, commentId);

        if (existingLike.isPresent()) {
            commentLikeRepository.delete(existingLike.get());
            comment.setLikes(Math.max(0, comment.getLikes() - 1));
        } else {
            CommentLike newLike = new CommentLike(userId, comment);
            commentLikeRepository.save(newLike);
            comment.setLikes(comment.getLikes() + 1);
        }
        commentRepository.save(comment);
    }

    /**
     * Retrieves a Comment entity by its ID.
     *
     * @param commentId The ID of the comment.
     * @return The Comment entity.
     * @throws IllegalArgumentException If comment is not found.
     */
    public Comment getCommentById(int commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment with ID " + commentId + " not found"));
    }

    /**
     * Deletes a comment by its ID (typically for administrative use).
     *
     * @param commentId The ID of the comment to delete.
     * @throws IllegalArgumentException If comment is not found.
     */
    public void deleteCommentAsAdmin(int commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment with ID " + commentId + " not found"));

        commentRepository.delete(comment);
    }

}