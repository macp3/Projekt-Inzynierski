package study.snacktrack.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

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

    // Pomocnicza metoda do formatowania nazwy
    private String formatAuthorName(User user) {
        return user.getName() + " " + user.getSurname();
    }

    // Pomocnicza metoda do pobierania usera (zamiast tylko nazwy)
    private User getUserById(int userId) {
        return userRepository.findById(userId).orElse(null);
    }

    private Comment validateCommentExistance(int commentId) {
        if (commentId <= 0) {
            throw new IllegalArgumentException("Invalid ID");
        }
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
    }

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

        // Zwracamy odpowiedź z Imieniem, Nazwiskiem i Zdjęciem
        return new CommentResponse(
                comment.getId(),
                author.getId(),
                formatAuthorName(author),
                author.getImageUrl(), // 🔹 ZDJĘCIE
                comment.getContent(),
                meal.getId(),
                0,
                false);
    }

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
                user.getImageUrl(), // 🔹 ZDJĘCIE
                comment.getContent(),
                comment.getMealId(),
                comment.getLikes(),
                isLiked);
    }

    public void deleteCommentByUser(int authorId, int mealId) {
        Comment comment = commentRepository.findByMealIdAndAuthorId(mealId, authorId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (comment.getAuthorId() != authorId) {
            throw new IllegalArgumentException("Not authorized");
        }
        commentRepository.delete(comment);
    }

    public List<CommentResponse> getAllCommentsByUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String userName = formatAuthorName(user);
        String userImage = user.getImageUrl(); // 🔹 ZDJĘCIE

        return commentRepository.findByAuthorId(userId).stream()
                .map(c -> {
                    boolean isLiked = commentLikeRepository.existsByUserIdAndCommentId(userId, c.getId());
                    return new CommentResponse(
                            c.getId(),
                            c.getAuthorId(),
                            userName,
                            userImage, // 🔹
                            c.getContent(),
                            c.getMealId(),
                            c.getLikes(),
                            isLiked);
                })
                .toList();
    }

    public Comment getUserMealComment(int mealId, int userId) {
        return commentRepository.findByMealIdAndAuthorId(mealId, userId)
                .orElseThrow(() -> new IllegalArgumentException("No comment found"));
    }

    public List<CommentResponse> getAllMealComments(int mealId, int currentUserId) {
        if (!mealRepository.existsById(mealId)) {
            throw new IllegalArgumentException("Meal not found");
        }

        List<Comment> comments = commentRepository.findByMealId(mealId);
        List<CommentResponse> response = new ArrayList<>();

        for (Comment c : comments) {
            boolean isLiked = commentLikeRepository.existsByUserIdAndCommentId(currentUserId, c.getId());

            // Pobieramy autora (imię i zdjęcie)
            User author = getUserById(c.getAuthorId());
            String name = (author != null) ? formatAuthorName(author) : "Unknown";
            String image = (author != null) ? author.getImageUrl() : null; // 🔹

            response.add(new CommentResponse(
                    c.getId(),
                    c.getAuthorId(),
                    name,
                    image, // 🔹
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
            commentLikeRepository.delete(existingLike.get());
            comment.setLikes(Math.max(0, comment.getLikes() - 1));
        } else {
            CommentLike newLike = new CommentLike(userId, comment);
            commentLikeRepository.save(newLike);
            comment.setLikes(comment.getLikes() + 1);
        }
        commentRepository.save(comment);
    }

    public Comment getCommentById(int commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment with ID " + commentId + " not found"));
    }

    public void deleteCommentAsAdmin(int commentId) {
        Comment comment = getCommentById(commentId);
        commentRepository.delete(comment);
    }
}