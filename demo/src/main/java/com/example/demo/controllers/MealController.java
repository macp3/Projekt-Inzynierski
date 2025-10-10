package com.example.demo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.MealRequest;
import com.example.demo.dto.MealResponse;
import com.example.demo.entities.Meal;
import com.example.demo.entities.User;
import com.example.demo.services.JwtService;
import com.example.demo.services.MealService;
import com.example.demo.services.UserService;

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

    @PostMapping("create")
    public ResponseEntity<Meal> createMeal(@RequestBody MealRequest request, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Meal meal = mealService.createMeal(request, user.getId());
        return ResponseEntity.ok(meal);
    }

    @GetMapping("")
    public ResponseEntity<List<Meal>> getMeals() {
        return ResponseEntity.ok(mealService.findAllMeals());
    }

    @GetMapping("/my")
    public ResponseEntity<List<Meal>> getUserMeals(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Meal> userMeals = mealService.getMealsByUser(user.getId());
        return ResponseEntity.ok(userMeals);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Meal>> searchMealsByName(@RequestParam String name) {
        return ResponseEntity.ok(mealService.searchMealsByName(name));
    }

    @GetMapping("/{mealId}/details")
    public ResponseEntity<MealResponse> getMealDetails(@PathVariable int mealId) {
        MealResponse response = mealService.getMealWithIngredients(mealId);
        return ResponseEntity.ok(response);
    }
}
