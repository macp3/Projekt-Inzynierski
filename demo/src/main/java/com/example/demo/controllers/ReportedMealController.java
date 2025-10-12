package com.example.demo.controllers;

import com.example.demo.dto.ReportedMealRequest;
import com.example.demo.dto.ReportedMealResponse;
import com.example.demo.entities.Meal;
import com.example.demo.entities.ReportedMeal;
import com.example.demo.entities.User;
import com.example.demo.services.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meals/reports")
public class ReportedMealController
{
    private final MealService mealService;
    private final JwtService jwtService;
    private final UserService userService;
    private final ReportedMealService reportedMealService;

    public ReportedMealController(MealService mealService, JwtService jwtService, UserService userService, CommentService commentService, ReportedMealService reportedMealService) {
        this.mealService = mealService;
        this.jwtService = jwtService;
        this.userService = userService;
        this.reportedMealService = reportedMealService;
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
    public ResponseEntity<ReportedMealResponse> reportMeal(@RequestBody ReportedMealRequest request, @RequestHeader("Authorization") String authHeader)
    {
        User user = authorizeUser(authHeader);
        Meal meal = mealService.getMealById(request.getMealId());

        ReportedMealResponse response = reportedMealService.reportMeal(request.getMealId(), user.getId(), request.getContent());
        return ResponseEntity.ok(response);
    }

    //admin
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReportedMeal>> getAllReportsByUser(@PathVariable int userId)
    {
        User user = userService.getUserById(userId);
        List<ReportedMeal> reports = reportedMealService.getAllReportsByUser(userId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/meal/{mealId}")
    public ResponseEntity<List<ReportedMeal>> getAllReportsByMeal(@PathVariable int mealId)
    {
        Meal meal = mealService.getMealById(mealId);
        List<ReportedMeal> reports = reportedMealService.getAllReportsByMeal(mealId);
        return ResponseEntity.ok(reports);
    }
}
