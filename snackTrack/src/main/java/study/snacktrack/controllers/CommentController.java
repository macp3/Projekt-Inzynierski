package study.snacktrack.controllers;

import study.snacktrack.dto.CommentRequest;
import study.snacktrack.entities.Comment;
import study.snacktrack.entities.User;
import study.snacktrack.services.CommentService;
import study.snacktrack.services.JwtService;
import study.snacktrack.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling all comment-related operations,
 * including adding, editing, deleting, liking, and viewing comments.
 */
@RestController
@RequestMapping("/comments")
public class CommentController {

    /** Service for JWT token handling and extraction. */
    private final JwtService jwtService;
    /** Service for user data access and retrieval. */
    private final UserService userService;
    /** Service for comment-specific business logic. */
    private final CommentService commentService;

    /**
     * Constructs the CommentController with required dependencies.
     */
    public CommentController(JwtService jwtService, UserService userService,
                             CommentService commentService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.commentService = commentService;
    }

    /**
     * Extracts the JWT from the Authorization header, validates it, and retrieves the corresponding User entity.
     *
     * @param authHeader The Authorization header containing the Bearer token.
     * @return The authenticated User entity.
     */
    private User authorizeUser(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        return userService.getUserByEmail(email);
    }

    /**
     * Adds a new comment to a specified meal.
     *
     * @param request The CommentRequest DTO containing mealId and content.
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity with the created Comment object or an error message.
     */
    @PostMapping("/add")
    public ResponseEntity<?> addCommentToMeal(@RequestBody CommentRequest request,
                                              @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authorizeUser(authHeader);
            return ResponseEntity.ok(commentService.addCommentToMeal(user.getId(), request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Edits an existing comment made by the authenticated user.
     *
     * @param request The CommentRequest DTO containing mealId and updated content.
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity with the updated Comment object or an error message.
     */
    @PutMapping("/edit")
    public ResponseEntity<?> editCommentByUser(@RequestBody CommentRequest request,
                                               @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authorizeUser(authHeader);
            Comment comment = commentService.getUserMealComment(request.getMealId(), user.getId());
            return ResponseEntity.ok(commentService.editComment(user.getId(), comment.getId(), request.getContent()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieves all comments created by the authenticated user.
     *
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity containing a list of comments or an error message.
     */
    @GetMapping("/my")
    public ResponseEntity<?> getAllCommentsByUser(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = authorizeUser(authHeader);
            return ResponseEntity.ok(commentService.getAllCommentsByUser(user.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Deletes a comment created by the authenticated user for a specific meal.
     *
     * @param mealId The ID of the meal associated with the comment.
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity with a success message or an error message.
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCommentByUser(
            @RequestParam int mealId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authorizeUser(authHeader);
            commentService.deleteCommentByUser(user.getId(), mealId);
            return ResponseEntity.ok("Comment deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieves all comments for a specific meal.
     * The returned response includes 'isLiked' status relative to the authenticated user.
     *
     * @param mealId The ID of the meal.
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity containing a list of CommentResponse DTOs or an error message.
     */
    @GetMapping("/meal/{mealId}")
    public ResponseEntity<?> getAllCommentsForMeal(
            @PathVariable int mealId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authorizeUser(authHeader);
            return ResponseEntity.ok(commentService.getAllMealComments(mealId, user.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Toggles the like status for a specific comment by the authenticated user.
     *
     * @param commentId The ID of the comment to like/unlike.
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity with a success message or an error message.
     */
    @PostMapping("/{commentId}/like")
    public ResponseEntity<String> toggleLike(
            @PathVariable int commentId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authorizeUser(authHeader);
            commentService.toggleLike(user.getId(), commentId);
            return ResponseEntity.ok("Like toggled");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}