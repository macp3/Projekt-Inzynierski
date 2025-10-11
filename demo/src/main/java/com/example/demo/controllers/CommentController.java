package com.example.demo.controllers;

import com.example.demo.dto.CommentRequest;
import com.example.demo.dto.CommentResponse;
import com.example.demo.entities.Comment;
import com.example.demo.entities.Meal;
import com.example.demo.entities.User;
import com.example.demo.services.CommentService;
import com.example.demo.services.JwtService;
import com.example.demo.services.MealService;
import com.example.demo.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController
{
    private final MealService mealService;
    private final JwtService jwtService;
    private final UserService userService;
    private final CommentService commentService;

    public CommentController(MealService mealService, JwtService jwtService, UserService userService, CommentService commentService) {
        this.mealService = mealService;
        this.jwtService = jwtService;
        this.userService = userService;
        this.commentService = commentService;
    }

    private User authorizeUser(String authHeader)
    {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user;
    }

    @PostMapping("/add")
    public ResponseEntity<CommentResponse> addCommentToMeal(@RequestBody CommentRequest request, @RequestHeader("Authorization") String authHeader)
    {
        User user = authorizeUser(authHeader);
        Meal meal = mealService.findMealById(request.getMealId());

        CommentResponse response = commentService.addCommentToMeal(user.getId(), request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit")
    public ResponseEntity<String> editCommentByUser(@RequestParam int mealId, @RequestParam int commentId, @RequestParam String content, @RequestHeader("Authorization") String authHeader)
    {
        User user = authorizeUser(authHeader);
        Comment comment = commentService.getUserMealComment(mealId, user.getId());

        boolean success = commentService.editComment(user.getId(), comment.getId(), content);
        if(success)
            return ResponseEntity.ok("Comment edited successfully");
        else
            return ResponseEntity.badRequest().body("Comment not edited (invalid data)");
    }

    @GetMapping("/my")
    public ResponseEntity<List<CommentResponse>> getAllCommentsByUser(@RequestHeader("Authorization") String authHeader) {
        User user = authorizeUser(authHeader);
        return ResponseEntity.ok(commentService.getAllCommentsByUser(user.getId()));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCommentByUser(
            @RequestParam int commentId,
            @RequestHeader("Authorization") String authHeader) {

        User user = authorizeUser(authHeader);
        commentService.deleteCommentByUser(user.getId(), commentId);

        return ResponseEntity.ok("Comment deleted successfully");
    }

    @GetMapping("/meal/{mealId}")
    public ResponseEntity<List<CommentResponse>> getAllCommentsForMeal(@PathVariable int mealId) {
        List<Comment> comments = commentService.getAllMealComments(mealId);
        List<CommentResponse> response = comments.stream()
                .map(c -> new CommentResponse(c.getId(), c.getAuthorId(), c.getContent(), c.getMealId()))
                .toList();
        return ResponseEntity.ok(response);
    }
}
