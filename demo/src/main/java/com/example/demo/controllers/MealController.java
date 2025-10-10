package com.example.demo.controllers;


import com.example.demo.dto.MealRequest;
import com.example.demo.entities.Meal;
import com.example.demo.entities.User;
import com.example.demo.services.JwtService;
import com.example.demo.services.MealService;
import com.example.demo.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meals")
public class MealController {

    private final MealService mealService;
    private final JwtService jwtService;
    private final UserService userService;

    public MealController(MealService mealService, JwtService jwtService, UserService userService) {
        this.mealService = mealService;
        this.jwtService = jwtService;

        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Meal> createMeal(@RequestBody MealRequest request) {
        Meal meal = mealService.createMeal(request);
        return ResponseEntity.ok(meal);
    }

    @GetMapping("")
    public ResponseEntity<List<Meal>> getMeals() {
        return ResponseEntity.ok(mealService.findAllMeals());
    }

    @GetMapping("/my")
    public ResponseEntity<List<Meal>> getUserMeals(@RequestHeader("Authorization") String authHeader)
    {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Meal> userMeals = mealService.getMealsByUser(user.getId());
        return ResponseEntity.ok(userMeals);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Meal>> searchMealsByName(@RequestParam String name)
    {
        return ResponseEntity.ok(mealService.searchMealsByName(name));
    }
}