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

import java.util.ArrayList;
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
        Meal meal = mealService.getMealById(request.getMealId());

        CommentResponse response = commentService.addCommentToMeal(user.getId(), request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit")
    public ResponseEntity<CommentResponse> editCommentByUser(@RequestBody CommentRequest request, @RequestHeader("Authorization") String authHeader)
    {
        User user = authorizeUser(authHeader);
        Comment comment = commentService.getUserMealComment(request.getMealId(), user.getId());

        CommentResponse cr = commentService.editComment(user.getId(), comment.getId(), request.getContent());
        return ResponseEntity.ok(cr);
    }

    @GetMapping("/my")
    public ResponseEntity<List<CommentResponse>> getAllCommentsByUser(@RequestHeader("Authorization") String authHeader) {
        User user = authorizeUser(authHeader);
        return ResponseEntity.ok(commentService.getAllCommentsByUser(user.getId()));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCommentByUser(
            @RequestParam int commentId,
            @RequestHeader("Authorization") String authHeader)
    {
        User user = authorizeUser(authHeader);
        commentService.deleteCommentByUser(user.getId(), commentId);

        return ResponseEntity.ok("Comment deleted successfully");
    }

    @GetMapping("/meal/{mealId}")
    public ResponseEntity<List<CommentResponse>> getAllCommentsForMeal(@PathVariable int mealId) {
        List<Comment> comments = commentService.getAllMealComments(mealId);
        List<CommentResponse> response = new ArrayList<>();

        for(Comment c : comments)
        {
            CommentResponse cr = new CommentResponse(c.getId(), c.getAuthorId(), c.getContent(), c.getMealId());
            response.add(cr);
        }
        return ResponseEntity.ok(response);
    }
}
