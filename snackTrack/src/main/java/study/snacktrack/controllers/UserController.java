package study.snacktrack.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import study.snacktrack.dto.BodyParametersRequest;
import study.snacktrack.dto.BodyParametersResponse;
import study.snacktrack.dto.LoginResponse;
import study.snacktrack.dto.NotificationResponse;
import study.snacktrack.entities.BodyParameters;
import study.snacktrack.entities.Favourite;
import study.snacktrack.entities.Meal;
import study.snacktrack.entities.User;
import study.snacktrack.repositories.BodyParametersRepository;
import study.snacktrack.repositories.UserRepository;
import study.snacktrack.services.JwtService;
import study.snacktrack.services.MealService;
import study.snacktrack.services.NotificationService;
import study.snacktrack.services.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing user-specific data, including profile details,
 * parameters, notifications, favourites, and profile image upload.
 */
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    /** Service for user-related business logic. */
    private final UserService userService;
    /** Service for JWT token handling. */
    private final JwtService jwtService;
    /** Service for notification retrieval. */
    private final NotificationService notificationService;
    /** Repository for User entity data access. */
    private final UserRepository userRepository;
    /** Repository for BodyParameters entity data access. */
    private final BodyParametersRepository bodyParametersRepository;
    /** Service for meal-related business logic (used for favourite meals). */
    private final MealService mealService;

    /**
     * Retrieves the authenticated user's profile information.
     *
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity containing the User entity or an error message.
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfileInfo(@RequestHeader("Authorization") String authHeader) {
        User user;
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);

            user = userService.getUserByEmail(email);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok(user);
    }

    /**
     * Retrieves the ID of the authenticated user.
     *
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity containing the user's ID or an error message.
     */
    @GetMapping("/getId")
    public ResponseEntity<?> getUserId(@RequestHeader("Authorization") String authHeader)
    {
        User user;
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);

            user = userService.getUserByEmail(email);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok(user.getId());
    }

    /**
     * Changes the password for the authenticated user.
     *
     * @param authHeader The Authorization header for user identification.
     * @param password The new password.
     * @return ResponseEntity with a success message or an error message.
     */
    @PutMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestHeader("Authorization") String authHeader,
                                                 @RequestParam String password) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);

            User user = userService.getUserByEmail(email);
            userService.changePassword(user.getId(), password);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Password changed successfully");
    }

    /**
     * Updates the existing body parameters for the authenticated user.
     *
     * @param authHeader The Authorization header for user identification.
     * @param request The BodyParametersRequest DTO with updated parameters.
     * @return ResponseEntity containing the updated BodyParametersResponse DTO or an error message.
     */
    @PutMapping("/changeParameters")
    public ResponseEntity<?> changeBodyParameters(@RequestHeader("Authorization") String authHeader,
                                                  @RequestBody BodyParametersRequest request) {
        BodyParametersResponse response;
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);

            User user = userService.getUserByEmail(email);

            response = userService.changeBodyParameters(user.getId(), request.getSex(), request.getHeight(),
                    request.getWeight(), request.getAge(), request.getDailyActivityFactor(),
                    request.getDailyActivityTrainingFactor(), request.getWeeklyWeightChangeTempo(),
                    request.getGoalWeight());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Adds initial body parameters for the authenticated user.
     *
     * @param authHeader The Authorization header for user identification.
     * @param request The BodyParametersRequest DTO with initial parameters.
     * @return ResponseEntity containing the created BodyParameters entity or an error message.
     */
    @PostMapping("/addParameters")
    public ResponseEntity<?> addBodyParameters(@RequestHeader("Authorization") String authHeader,
                                               @RequestBody BodyParametersRequest request) {
        BodyParameters response;
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);

            User user = userService.getUserByEmail(email);

            response = userService.addBodyParameters(user.getId(), request.getSex(), request.getHeight(),
                    request.getWeight(), request.getAge(), request.getDailyActivityFactor(),
                    request.getDailyActivityTrainingFactor(), request.getWeeklyWeightChangeTempo(),
                    request.getGoalWeight());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Checks if the user has completed the initial body parameters survey.
     * Returns a LoginResponse to refresh client state (including survey flag).
     *
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity containing LoginResponse with the survey status.
     */
    @GetMapping("/refreshSurvey")
    public ResponseEntity<?> refreshSurvey(@RequestHeader("Authorization") String authHeader) {
        String token;
        boolean showSurvey;
        try {

            token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            User user = userService.getUserByEmail(email);

            showSurvey = !bodyParametersRepository.existsByUserId(user.getId());
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body("Something went wrong");
        }

        return ResponseEntity.ok(new LoginResponse(token, showSurvey, null));
    }

    /**
     * Retrieves the current login streak count for the authenticated user.
     *
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity containing the streak count or an error message.
     */
    @GetMapping("/myStreak")
    public ResponseEntity<?> getMyStreak(@RequestHeader("Authorization") String authHeader) {
        User user;
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);

            user = userService.getUserByEmail(email);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok(user.getStreak());
    }

    /**
     * Retrieves a list of notifications for the authenticated user.
     *
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity containing a list of NotificationResponse DTOs or an error message.
     */
    @GetMapping("/notifications")
    public ResponseEntity<?> getUserNotifications(
            @RequestHeader("Authorization") String authHeader) {
        List<NotificationResponse> response;
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);

            User user = userService.getUserByEmail(email);
            response = notificationService.getNotificationsByUser(user.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Saves the device token (FCM token) for the authenticated user to enable push notifications.
     *
     * @param authHeader The Authorization header for user identification.
     * @param request A map containing the device token under the key "token".
     * @return ResponseEntity with a success message or an error message.
     */
    @PostMapping("/device-token")
    public ResponseEntity<String> saveDeviceToken(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            User user = userService.getUserByEmail(email);

            String deviceToken = request.get("token");
            userService.saveDeviceToken(user.getId(), deviceToken);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok("Device token saved");
    }

    /**
     * Adds a meal to the authenticated user's favourite list.
     *
     * @param mealId The ID of the meal to add as favourite.
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity containing the saved Favourite entity or an error message.
     */
    @PostMapping("/favourite/add")
    public ResponseEntity<?> addFavourite(@RequestParam int mealId,
                                          @RequestHeader("Authorization") String authHeader) {
        Favourite saved;
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            Meal meal = mealService.getMealById(mealId);
            User user = userService.getUserByEmail(email);
            saved = userService.addFavourite(meal.getId(), user.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(saved);
    }

    /**
     * Removes a meal from the authenticated user's favourite list.
     *
     * @param mealId The ID of the meal to remove from favourites.
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity with a success message or an error message.
     */
    @DeleteMapping("/favourite/remove/{mealId}")
    public ResponseEntity<String> removeFavourite(@PathVariable int mealId,
                                                  @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            User user = userService.getUserByEmail(email);
            Meal meal = mealService.getMealById(mealId);

            userService.removeFavourite(meal.getId(), user.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Favourite removed successfully");
    }

    /**
     * Retrieves all favourite meals for the authenticated user.
     *
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity containing a list of Meal entities or an error message.
     */
    @GetMapping("/favourite")
    public ResponseEntity<?> getMyFavouriteMeals(@RequestHeader("Authorization") String authHeader) {
        List<Meal> meals;
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            User user = userService.getUserByEmail(email);
            meals = userService.getMyFavouriteMeals(user.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok(meals);
    }

    /**
     * Uploads a new profile image for the authenticated user.
     *
     * @param authHeader The Authorization header for user identification.
     * @param imageFile The MultipartFile representing the image.
     * @return ResponseEntity with the URL of the uploaded image or an error message.
     * @throws IOException If there is an issue handling the file upload.
     */
    @PostMapping("/image")
    public ResponseEntity<String> uploadProfileImage(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("image") MultipartFile imageFile) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            User user = userService.getUserByEmail(email);
            String imageUrl = userService.uploadProfileImage(user.getId(), imageFile);
            return ResponseEntity.ok(imageUrl);
        } catch (RuntimeException | IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieves the latest body parameters and calculated macronutrient goals for the authenticated user.
     *
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity containing the BodyParametersResponse DTO or an error message.
     */
    @GetMapping("/bodyParameters")
    public ResponseEntity<?> getBodyParameters(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);

            User user = userService.getUserByEmail(email);
            BodyParametersResponse response = userService.getUserBodyParametersResponse(user.getId());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Updates the premium expiration date for the authenticated user.
     * NOTE: This is likely a test or admin function and should be secured in a production environment.
     *
     * @param authHeader The Authorization header for user identification.
     * @param expirationDate The new premium expiration date string.
     * @return ResponseEntity with a success message or an error message.
     */
    @PutMapping("/premium")
    public ResponseEntity<?> updatePremium(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("expiration") String expirationDate) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            User user = userService.getUserByEmail(email);

            User updated = userService.updatePremiumExpiration(user.getId(), expirationDate);
            userRepository.save(updated);

            return ResponseEntity.ok("Premium updated");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}