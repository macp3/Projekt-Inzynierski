package com.example.demo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.MealRequest;
import com.example.demo.dto.MealResponse;
import com.example.demo.entities.Comment;
import com.example.demo.entities.Meal;
import com.example.demo.entities.User;
import com.example.demo.services.CommentService;
import com.example.demo.services.JwtService;
import com.example.demo.services.MealService;
import com.example.demo.services.UserService;

@RestController
@RequestMapping("/meals")
public class MealController {

    private final MealService mealService;
    private final JwtService jwtService;
    private final UserService userService;
    private final CommentService commentService;

    public MealController(MealService mealService, JwtService jwtService, UserService userService, CommentService commentService) {
        this.mealService = mealService;
        this.jwtService = jwtService;
        this.userService = userService;
        this.commentService = commentService;
    }

    private User authorizeUser(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email);
        return user;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createMeal(@RequestBody MealRequest request, @RequestHeader("Authorization") String authHeader) {
        User user = authorizeUser(authHeader);

        boolean success = mealService.createMeal(request, user.getId());
        if (!success) {
            return ResponseEntity.badRequest().body("Meal not created (invalid data)");
        } else {
            return ResponseEntity.ok("Meal successfully created");
        }
    }

    @PutMapping("/my/edit")
    public ResponseEntity<String> editMealByUser(@RequestParam int mealId, @RequestBody MealRequest request, @RequestHeader("Authorization") String authHeader) {
        User user = authorizeUser(authHeader);

        boolean success = mealService.editMealByUser(mealId, request, user.getId());
        if (success) {
            return ResponseEntity.ok("Meal successfully edited");
        } else {
            return ResponseEntity.ok("Meal not edited (invalid data)");
        }
    }

    //tutaj tak samo - podejscie dto
    @PutMapping("/my/delete")
    public ResponseEntity<String> deleteMealByUser(@RequestParam int mealId, @RequestHeader("Authorization") String authHeader) {
        User user = authorizeUser(authHeader);

        boolean success = mealService.deleteMealByUser(mealId, user.getId());
        if (success) {
            return ResponseEntity.ok("Meal successfully deleted");
        } else {
            return ResponseEntity.ok("Meal not deleted (invalid data)");
        }
    }

    @GetMapping("")
    public ResponseEntity<List<Meal>> getMeals() {
        return ResponseEntity.ok(mealService.getAllMeals());
    }

    @GetMapping("/my")
    public ResponseEntity<List<Meal>> getUserMeals(@RequestHeader("Authorization") String authHeader) {
        User user = authorizeUser(authHeader);
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

    //nie wiem dobrze?????
    @GetMapping("/{mealId}/comments")
    public ResponseEntity<List<Comment>> getMealComments(@PathVariable int mealId) {
        return ResponseEntity.ok(commentService.getAllMealComments(mealId));
    }
}
