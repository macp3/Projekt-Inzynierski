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
import study.snacktrack.services.JwtService;
import study.snacktrack.services.MealService;
import study.snacktrack.services.UserService;

/**
 * REST controller for handling meal-related operations, including creation,
 * editing, deletion, viewing, and image uploads.
 */
@RestController
@RequestMapping("/meals")
public class MealController {

    /** Service for meal-specific business logic. */
    private final MealService mealService;
    /** Service for JWT token handling. */
    private final JwtService jwtService;
    /** Service for user data access and retrieval. */
    private final UserService userService;


    /**
     * Constructs the MealController with required dependencies.
     */
    public MealController(MealService mealService, JwtService jwtService, UserService userService) {
        this.mealService = mealService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    /**
     * Extracts the JWT from the Authorization header, validates it, and retrieves the corresponding User entity.
     *
     * @param authHeader The Authorization header containing the Bearer token.
     * @return The authenticated User entity.
     */
    private User authorizeUser(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email);
        return user;
    }

    /**
     * Creates a new meal entry for the authenticated user.
     *
     * @param request The MealRequest DTO containing meal details.
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity with the created Meal entity or an error message.
     */
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

    /**
     * Edits an existing meal created by the authenticated user.
     *
     * @param mealId The ID of the meal to edit.
     * @param request The MealRequest DTO containing updated details.
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity with a success message or an error message.
     */
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

    /**
     * Deletes a meal created by the authenticated user.
     *
     * @param mealId The ID of the meal to delete.
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity with a success message or an error message.
     */
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

    /**
     * Retrieves a list of all meals available in the system.
     *
     * @param authHeader The Authorization header (used for authentication check).
     * @return ResponseEntity containing a list of all Meal entities.
     */
    @GetMapping("")
    public ResponseEntity<List<Meal>> getMeals(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(mealService.getAllMeals());
    }

    /**
     * Retrieves all meals created by the authenticated user.
     *
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity containing a list of the user's Meal entities.
     */
    @GetMapping("/my")
    public ResponseEntity<List<Meal>> getUserMeals(@RequestHeader("Authorization") String authHeader) {
        User user = authorizeUser(authHeader);
        List<Meal> userMeals = mealService.getMealsByUser(user.getId());
        return ResponseEntity.ok(userMeals);
    }

    /**
     * Searches for meals by name.
     *
     * @param name The name or partial name of the meal to search for.
     * @return ResponseEntity containing a list of matching Meal entities.
     */
    @GetMapping("/search")
    public ResponseEntity<List<Meal>> searchMealsByName(@RequestParam String name) {
        return ResponseEntity.ok(mealService.searchMealsByName(name));
    }

    /**
     * Retrieves detailed information about a specific meal, including its ingredients.
     *
     * @param mealId The ID of the meal.
     * @param authHeader The Authorization header (used for authentication check).
     * @return ResponseEntity containing the MealResponse DTO or an error message.
     */
    @GetMapping("/{mealId}/details")
    public ResponseEntity<?> getMealDetails(
            @PathVariable int mealId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            authorizeUser(authHeader);

            MealResponse response = mealService.getMealWithIngredients(mealId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Uploads an image for a specific meal created by the authenticated user.
     *
     * @param mealId The ID of the meal to upload the image for.
     * @param imageFile The MultipartFile representing the image.
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity with the URL of the uploaded image or an error message.
     * @throws IOException If there is an issue handling the file upload.
     */
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