package study.snacktrack.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import study.snacktrack.dto.*;
import study.snacktrack.entities.BodyParameters;
import study.snacktrack.entities.Favourite;
import study.snacktrack.entities.Meal;
import study.snacktrack.entities.Notification;
import study.snacktrack.entities.User;
import study.snacktrack.repositories.BodyParametersRepository;
import study.snacktrack.repositories.FavouriteRepository;
import study.snacktrack.repositories.MealRepository;
import study.snacktrack.repositories.UserRepository;
import study.snacktrack.services.JwtService;
import study.snacktrack.services.MealService;
import study.snacktrack.services.NotificationService;
import study.snacktrack.services.UserService;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final BodyParametersRepository bodyParametersRepository;
    private final MealService mealService;

    // premium expiration musi byc nullem po dacie ktora jest w bazie
    // dziala
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

    // funkcja wylacznie dla admina
    /*
     * @GetMapping("/{id}")
     * public ResponseEntity<User> getUserInfo(@RequestHeader("Authorization")
     * String authHeader)
     * {
     * String token = authHeader.replace("Bearer ", "");
     * String email = jwtService.extractEmail(token);
     * 
     * User user = userService.getUserByEmail(email)
     * .orElseThrow(() -> new RuntimeException("User not found"));
     * return ResponseEntity.ok(user);
     * }
     */

    // dziala
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

    // dziala
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

    // dziala
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

    // dziala
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

    // dziala
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

    // nie wiem jak to przetestowac - zostawiam
    //
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

    // do serwisuuuuuuuuu z logika
    // zmienilem - przetestowac
    // dziala
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

    // do serwisu z logika
    // zmienilem - przetestowac
    // dziala
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

    // dziala
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

    // dziala
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

    // Maciej: Funkcja testowa. W finalnej wersji powinna być zabezpieczona
    // sprawdzeniem płatności
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
