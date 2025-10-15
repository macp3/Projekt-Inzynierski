package com.example.demo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ReportedMealRequest;
import com.example.demo.dto.ReportedMealResponse;
import com.example.demo.entities.ReportedMeal;
import com.example.demo.entities.User;
import com.example.demo.services.CommentService;
import com.example.demo.services.JwtService;
import com.example.demo.services.MealService;
import com.example.demo.services.ReportedMealService;
import com.example.demo.services.UserService;

@RestController
@RequestMapping("/meals/reports")
public class ReportedMealController {

    private final JwtService jwtService;
    private final UserService userService;
    private final ReportedMealService reportedMealService;

    public ReportedMealController(MealService mealService, JwtService jwtService, UserService userService, CommentService commentService, ReportedMealService reportedMealService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.reportedMealService = reportedMealService;
    }

    private User authorizeUser(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email);
        return user;
    }

    @PostMapping("/add")
    public ResponseEntity<ReportedMealResponse> reportMeal(@RequestBody ReportedMealRequest request, @RequestHeader("Authorization") String authHeader) {
        User user = authorizeUser(authHeader);

        ReportedMealResponse response = reportedMealService.reportMeal(request.getMealId(), user.getId(), request.getContent());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/meal/{mealId}")
    public ResponseEntity<List<ReportedMeal>> getAllReportsByMeal(@PathVariable int mealId) {
        List<ReportedMeal> reports = reportedMealService.getAllReportsByMeal(mealId);
        return ResponseEntity.ok(reports);
    }
}
