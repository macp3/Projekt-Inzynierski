package study.snacktrack.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

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
import org.springframework.web.multipart.MultipartFile;

import study.snacktrack.dto.AssignDietTypesRequest;
import study.snacktrack.dto.MealRequest;
import study.snacktrack.dto.MealResponse;
import study.snacktrack.entities.Comment;
import study.snacktrack.entities.DietType;
import study.snacktrack.entities.Meal;
import study.snacktrack.entities.MealDietType;
import study.snacktrack.entities.User;
import study.snacktrack.repositories.DietTypeRepository;
import study.snacktrack.repositories.MealDietTypeRepository;
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
    private final MealDietTypeRepository mealDietTypeRepository;
    private final DietTypeRepository dietTypeRepository;

    public MealController(MealService mealService, JwtService jwtService, UserService userService, CommentService commentService, MealDietTypeRepository mealDietTypeRepository, DietTypeRepository dietTypeRepository) {
        this.mealService = mealService;
        this.jwtService = jwtService;
        this.userService = userService;
        this.commentService = commentService;
        this.mealDietTypeRepository = mealDietTypeRepository;
        this.dietTypeRepository = dietTypeRepository;
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

    @PostMapping("/{mealId}/image")
    public ResponseEntity<String> uploadMealImage(
            @PathVariable int mealId,
            @RequestParam("image") MultipartFile imageFile) {
        try {
            String imageUrl = mealService.uploadMealImage(mealId, imageFile);
            return ResponseEntity.ok(imageUrl);
        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{mealId}/diet-types")
    public ResponseEntity<String> assignDietTypes(
            @PathVariable int mealId,
            @RequestBody AssignDietTypesRequest request) {
        try {
            mealService.assignDietTypesToMeal(mealId, request.getDietTypeIds());
            return ResponseEntity.ok("Diet types assigned successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{mealId}/diet-types")
    public ResponseEntity<List<DietType>> getDietTypesForMeal(@PathVariable int mealId) {
        List<MealDietType> mappings = mealDietTypeRepository.findByMealId(mealId);
        List<DietType> dietTypes = mappings.stream()
                .map(mdt -> dietTypeRepository.findById(mdt.getDietTypeId()).orElse(null))
                .filter(Objects::nonNull)
                .toList();
        return ResponseEntity.ok(dietTypes);
    }
}
