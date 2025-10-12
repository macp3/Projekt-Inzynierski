package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entities.User;
import com.example.demo.services.CommentService;
import com.example.demo.services.FoodService;
import com.example.demo.services.JwtService;
import com.example.demo.services.MealService;
import com.example.demo.services.ReportedCommentService;
import com.example.demo.services.ReportedMealService;
import com.example.demo.services.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final MealService mealService;
    private final CommentService commentService;
    private final ReportedMealService reportedMealService;
    private final ReportedCommentService reportedCommentService;
    private final FoodService foodService;
    private final JwtService jwtService;

    public AdminController(UserService userService, MealService mealService, CommentService commentService, ReportedMealService reportedMealService, ReportedCommentService reportedCommentService, FoodService foodService, JwtService jwtService) {
        this.userService = userService;
        this.mealService = mealService;
        this.commentService = commentService;
        this.reportedMealService = reportedMealService;
        this.reportedCommentService = reportedCommentService;
        this.foodService = foodService;
        this.jwtService = jwtService;
    }

    @GetMapping("/dashboard")
    public String getDashboard() {
        return "Witaj, ADMIN!";
    }

    //nowe
    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUserInfo(@RequestHeader("Authorization") String authHeader, @PathVariable int userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    //doi naprawy - updatowanie streaka: albo +1 albo wyzerowac
    @GetMapping("/{userId}/updateStreak")
    public ResponseEntity<User> updateStreak(@PathVariable int userId, @RequestParam int streak) {

        User user = userService.getUserById(userId);

        user.setStreak(streak);
        userService.updateStreak(userId, streak);
        return ResponseEntity.ok(user);
    }
}
