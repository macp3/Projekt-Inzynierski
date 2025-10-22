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
import study.snacktrack.dto.AddFavouriteRequest;
import study.snacktrack.dto.BodyParametersRequest;
import study.snacktrack.dto.BodyParametersResponse;
import study.snacktrack.dto.NotificationResponse;
import study.snacktrack.entities.BodyParameters;
import study.snacktrack.entities.Favourite;
import study.snacktrack.entities.Meal;
import study.snacktrack.entities.Notification;
import study.snacktrack.entities.User;
import study.snacktrack.entities.enums.DietTypes;
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
    private final FavouriteRepository favouriteRepository;
    private final MealRepository mealRepository;
    private final MealService mealService;

    //admin
    @ResponseBody
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    //dziala
    @GetMapping("/profile")
    public ResponseEntity<User> getProfileInfo(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    //funkcja wylacznie dla admina
    /*@GetMapping("/{id}")
    public ResponseEntity<User> getUserInfo(@RequestHeader("Authorization") String authHeader)
    {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }*/

    //DZIALAAAAAAAAA
    @PutMapping("/changePassword")
    public ResponseEntity<User> changePassword(@RequestHeader("Authorization") String authHeader, @RequestParam String password) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email);
        userService.changePassword(user.getId(), password);
        return ResponseEntity.ok(user);
    }


    //dziala
    @PutMapping("/changeParameters")
    public ResponseEntity<BodyParametersResponse> changeBodyParameters(@RequestHeader("Authorization") String authHeader, @RequestBody BodyParametersRequest request) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email);

        BodyParametersResponse response
                = userService.changeBodyParameters(user.getId(), request.getSex(), request.getHeight(), request.getWeight(), request.getAge(), request.getDailyActivityFactor(), request.getDailyActivityTrainingFactor(), request.getWeeklyWeightChangeTempo(), request.getGoalWeight());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/changePrefferedDiet")
    public ResponseEntity<User> changePrefferedDiet(@RequestHeader("Authorization") String authHeader, DietTypes prefferedDiet) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email);

        userService.changePrefferedDiet(user.getId(), prefferedDiet);
        return ResponseEntity.ok(user);
    }

    //dziala
    @PostMapping("/addParameters")
    public ResponseEntity<BodyParameters> addBodyParameters(@RequestHeader("Authorization") String authHeader, @RequestBody BodyParametersRequest request) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email);

        BodyParameters response
                = userService.addBodyParameters(user.getId(), request.getSex(), request.getHeight(), request.getWeight(), request.getAge(), request.getDailyActivityFactor(), request.getDailyActivityTrainingFactor(), request.getWeeklyWeightChangeTempo(), request.getGoalWeight());
        return ResponseEntity.ok(response);
    }

    //dziala
    @GetMapping("/myStreak")
    public ResponseEntity<Integer> getMyStreak(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email);

        return ResponseEntity.ok(user.getStreak());
    }

    //dziala ale fajnie by bylo to do serwisu przeniesc zeby kontrolery byly czysciutkie
    //zmienilem - przetestowac
    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(
            @RequestHeader("Authorization") String authHeader
    )
    {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email);
        List<NotificationResponse> response = notificationService.getNotificationsByUser(user.getId());

        return ResponseEntity.ok(response);
    }


    //nie wiem jak to przetestowac - zostawiam
    @PostMapping("/device-token")
    public ResponseEntity<String> saveDeviceToken(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request
    ) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        User user = userService.getUserByEmail(email);

        String deviceToken = request.get("token");
        userService.saveDeviceToken(user.getId(), deviceToken);

        return ResponseEntity.ok("Device token saved");
    }

    //do serwisuuuuuuuuu z logika
    //zmienilem - przetestowac
    @PostMapping("/favourite/add")
    public ResponseEntity<Favourite> addFavourite(@RequestParam int mealId,
            @RequestHeader("Authorization") String authHeader)
    {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        Meal meal = mealService.getMealById(mealId);
        User user = userService.getUserByEmail(email);
        Favourite saved = userService.addFavourite(meal.getId(), user.getId());

        return ResponseEntity.ok(saved);
    }

    //do serwisu z logika
    //zmienilem - przetestowac
    @DeleteMapping("/favourite/remove/{mealId}")
    public ResponseEntity<String> removeFavourite(@PathVariable int mealId, @RequestHeader("Authorization") String authHeader)
    {
        try
        {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            User user = userService.getUserByEmail(email);
            Meal meal = mealService.getMealById(mealId);

            userService.removeFavourite(meal.getId(), user.getId());
        }
        catch(IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Favourite removed successfully");
    }

    //do serwisu z logika
    //
    @GetMapping("/favourite")
    public ResponseEntity<?> getMyFavouriteMeals(@RequestHeader("Authorization") String authHeader)
    {
        List<Meal> meals = new ArrayList<>();
        try
        {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            User user = userService.getUserByEmail(email);
            meals = userService.getMyFavouriteMeals(user.getId());
        }
        catch(IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok(meals);
    }

    //do serwisu z logika
    //
    @PostMapping("/image")
    public ResponseEntity<String> uploadMealImage(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("image") MultipartFile imageFile)
    {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            User user = userService.getUserByEmail(email);
            String imageUrl = userService.uploadProfileImage(user.getId(), imageFile);
            return ResponseEntity.ok(imageUrl);
        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
