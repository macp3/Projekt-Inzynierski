package study.snacktrack.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import study.snacktrack.dto.CommentRequest;
import study.snacktrack.dto.CommentResponse;
import study.snacktrack.entities.Comment;
import study.snacktrack.entities.CommentLike;
import study.snacktrack.entities.Meal;
import study.snacktrack.repositories.UserRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import study.snacktrack.entities.User;
import study.snacktrack.repositories.CommentRepository;
import study.snacktrack.repositories.CommentLikeRepository;
import study.snacktrack.repositories.MealRepository;

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

    private Comment validateCommentExistance(int commentId) {
        if (commentId <= 0) {
            throw new IllegalArgumentException("Comment ID must be greater than zero");
        }

        Optional<Comment> optionalComment = commentRepository.findById(commentId);

        if (optionalComment.isEmpty()) {
            throw new IllegalArgumentException("Comment with specified ID doesn't exist");
        }

        return optionalComment.get();
    }

    public CommentResponse addCommentToMeal(int authorId, CommentRequest request) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Meal meal = mealRepository.findById(request.getMealId())
                .orElseThrow(() -> new IllegalArgumentException("Meal not found"));

        // Uncomment if you want to prevent authors from commenting on their own meals
        // if (author.getId() == meal.getAuthorId())
        // throw new IllegalArgumentException("You cannot comment your own meal");

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
        // Default likes is 0
        commentRepository.save(comment);

        // Return basic response (likes=0, isLiked=false)
        return new CommentResponse(comment.getId(), author.getId(), comment.getContent(), meal.getId(), 0, false);
    }

    public CommentResponse editComment(int authorId, int commentId, String content) {
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("There is no user with specified ID"));

        Comment comment = validateCommentExistance(commentId);

        if (user.getId() != comment.getAuthorId()) {
            throw new IllegalArgumentException("You are not authorized to edit this comment");
        }

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content must not be empty");
        }

        comment.setContent(content);
        commentRepository.save(comment);

        // We need to check if the user liked their own comment (unlikely but possible
        // via API)
        boolean isLiked = commentLikeRepository.existsByUserIdAndCommentId(authorId, commentId);

        return new CommentResponse(
                comment.getId(),
                authorId,
                comment.getContent(),
                comment.getMealId(),
                comment.getLikes(),
                isLiked);
    }

    public void deleteCommentByUser(int authorId, int mealId) {
        Comment comment = commentRepository.findByMealIdAndAuthorId(mealId, authorId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (comment.getAuthorId() != authorId) {
            throw new IllegalArgumentException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
        System.out.println("Comment deleted successfully");
    }

    public List<CommentResponse> getAllCommentsByUser(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }

        return commentRepository.findByAuthorId(userId)
                .stream()
                .map(c -> {
                    // For "my comments", we check if I liked them myself
                    boolean isLiked = commentLikeRepository.existsByUserIdAndCommentId(userId, c.getId());
                    return new CommentResponse(c.getId(), c.getAuthorId(), c.getContent(), c.getMealId(), c.getLikes(),
                            isLiked);
                })
                .toList();
    }

    public Comment getUserMealComment(int mealId, int userId) {
        return commentRepository.findByMealIdAndAuthorId(mealId, userId)
                .orElseThrow(() -> new IllegalArgumentException("This user has no registered comment for this meal"));
    }

    public Comment getCommentById(int commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("There is no comment with specified ID"));
    }

    // Main method to get comments for a meal with 'isLiked' status for the current
    // user
    public List<CommentResponse> getAllMealComments(int mealId, int currentUserId) {
        if (!mealRepository.existsById(mealId)) {
            throw new IllegalArgumentException("Meal not found");
        }

        List<Comment> comments = commentRepository.findByMealId(mealId);
        List<CommentResponse> response = new ArrayList<>();

        for (Comment c : comments) {
            // Check if the current user liked this comment
            boolean isLiked = commentLikeRepository.existsByUserIdAndCommentId(currentUserId, c.getId());

            response.add(new CommentResponse(
                    c.getId(),
                    c.getAuthorId(),
                    c.getContent(),
                    c.getMealId(),
                    c.getLikes(),
                    isLiked));
        }
        return response;
    }

    @Transactional
    public void toggleLike(int userId, int commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        Optional<CommentLike> existingLike = commentLikeRepository.findByUserIdAndCommentId(userId, commentId);

        if (existingLike.isPresent()) {
            // Unlike
            commentLikeRepository.delete(existingLike.get());
            comment.setLikes(Math.max(0, comment.getLikes() - 1));
        } else {
            // Like
            CommentLike newLike = new CommentLike(userId, comment);
            commentLikeRepository.save(newLike);
            comment.setLikes(comment.getLikes() + 1);
        }
        commentRepository.save(comment);
    }
}