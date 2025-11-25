package study.snacktrack.controllers;

import java.util.List;

import study.snacktrack.dto.CommentRequest;
import study.snacktrack.dto.CommentResponse;
import study.snacktrack.entities.Comment;
import study.snacktrack.entities.User;
import study.snacktrack.services.CommentService;
import study.snacktrack.services.JwtService;
import study.snacktrack.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final JwtService jwtService;
    private final UserService userService;
    private final CommentService commentService;

    public CommentController(JwtService jwtService, UserService userService,
            CommentService commentService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.commentService = commentService;
    }

    private User authorizeUser(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        return userService.getUserByEmail(email);
    }

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

    @GetMapping("/my")
    public ResponseEntity<?> getAllCommentsByUser(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = authorizeUser(authHeader);
            return ResponseEntity.ok(commentService.getAllCommentsByUser(user.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

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

    // 🔹 UPDATED: Now requires Authorization header to check 'isLiked' status
    @GetMapping("/meal/{mealId}")
    public ResponseEntity<?> getAllCommentsForMeal(
            @PathVariable int mealId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // 1. Identify user
            User user = authorizeUser(authHeader);

            // 2. Pass user ID to service to populate 'isLiked' correctly
            return ResponseEntity.ok(commentService.getAllMealComments(mealId, user.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

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