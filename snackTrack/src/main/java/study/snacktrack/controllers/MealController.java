package study.snacktrack.controllers;

import java.io.IOException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import study.snacktrack.dto.MealRequest;
import study.snacktrack.dto.MealResponse;
import study.snacktrack.entities.Meal;
import study.snacktrack.entities.User;
import study.snacktrack.services.CommentService;
import study.snacktrack.services.JwtService;
import study.snacktrack.services.MealService;
import study.snacktrack.services.UserService;

@RestController
@RequestMapping("/meals")
public class MealController {

    private final MealService mealService;
    private final JwtService jwtService;
    private final UserService userService;
    private final CommentService commentService;

    public MealController(MealService mealService, JwtService jwtService, UserService userService,
            CommentService commentService) {
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

    // dziala
    @PostMapping("/create")
    public ResponseEntity<?> createMeal(@RequestBody MealRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authorizeUser(authHeader);

            return ResponseEntity.ok(mealService.createMeal(request, user.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // dziala
    @PutMapping("/my/edit/{mealId}")
    public ResponseEntity<String> editMealByUser(@PathVariable int mealId, @RequestBody MealRequest request,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = authorizeUser(authHeader);

            return ResponseEntity.ok(mealService.editMealByUser(mealId, request, user.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // dziala
    @DeleteMapping("/my/delete/{mealId}")
    public ResponseEntity<String> deleteMealByUser(@PathVariable int mealId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authorizeUser(authHeader);

            return ResponseEntity.ok(mealService.deleteMealByUser(mealId, user.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // dziala
    @GetMapping("")
    public ResponseEntity<List<Meal>> getMeals(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(mealService.getAllMeals());
    }

    // dziala
    @GetMapping("/my")
    public ResponseEntity<List<Meal>> getUserMeals(@RequestHeader("Authorization") String authHeader) {
        User user = authorizeUser(authHeader);
        List<Meal> userMeals = mealService.getMealsByUser(user.getId());
        return ResponseEntity.ok(userMeals);
    }

    // dziala
    @GetMapping("/search")
    public ResponseEntity<List<Meal>> searchMealsByName(@RequestParam String name) {
        return ResponseEntity.ok(mealService.searchMealsByName(name));
    }

    @GetMapping("/{mealId}/details")
    public ResponseEntity<?> getMealDetails(
            @PathVariable int mealId,
            @RequestHeader("Authorization") String authHeader) { // <--- DODAJ TO
        try {
            authorizeUser(authHeader);

            MealResponse response = mealService.getMealWithIngredients(mealId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // dziala
    @PostMapping("/{mealId}/image")
    public ResponseEntity<String> uploadMealImage(
            @PathVariable int mealId,
            @RequestParam("image") MultipartFile imageFile,
            @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authorizeUser(authHeader);
            String imageUrl = mealService.uploadMealImage(mealId, imageFile, user.getId());
            return ResponseEntity.ok(imageUrl);
        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
