package study.snacktrack.controllers;

import java.util.List;

import study.snacktrack.dto.EssentialFoodRequest;
import study.snacktrack.dto.EssentialFoodResponse;
import study.snacktrack.dto.UnifiedSearchResponse;
import study.snacktrack.entities.EssentialFood;
import study.snacktrack.services.FoodService;
import study.snacktrack.services.JwtService;
import study.snacktrack.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import study.snacktrack.dto.ApiFoodResponse;
import study.snacktrack.entities.User;

/**
 * REST controller for managing and retrieving food data, both from local database
 * (EssentialFood) and external API sources.
 */
@RestController
@RequestMapping("/food")
public class FoodController {

    /** Service layer for food-related business logic. */
    private final FoodService foodService;
    /** Service layer for user-related business logic. */
    private final UserService userService;
    /** Service for JWT token handling. */
    private final JwtService jwtService;

    /**
     * Constructs the FoodController with required dependencies.
     */
    public FoodController(FoodService foodService, UserService userService, JwtService jwtService) {
        this.foodService = foodService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * Retrieves all "essential" foods from the local database.
     *
     * @return List of EssentialFoodResponse DTOs.
     */
    @GetMapping("/all")
    public List<EssentialFoodResponse> getAllEssentials() {
        return foodService.getAllEssentials();
    }

    /**
     * Searches for food items using an external API based on a query string.
     *
     * @param query The search query.
     * @return List of ApiFoodResponse DTOs.
     */
    @GetMapping("/api/search")
    public List<ApiFoodResponse> getFoodFromApi(@RequestParam String query) {
        return foodService.getFoodFromApi(query);
    }

    /**
     * Retrieves detailed information about a food item from the external API by ID.
     *
     * @param id The unique ID of the food item in the external API.
     * @return ResponseEntity with the detailed food object or an error message.
     */
    @GetMapping("/api/{id}")
    public ResponseEntity<?> getFoodFromApiById(@PathVariable("id") int id) {
        try {
            return ResponseEntity.ok(foodService.getFoodFromApiById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Adds a new essential food item to the local database.
     * Requires user authentication via the Authorization header.
     *
     * @param request The EssentialFoodRequest DTO.
     * @param authHeader The Authorization header containing the JWT token.
     * @return ResponseEntity with the created EssentialFood object or an error message.
     */
    @PostMapping("/add")
    public ResponseEntity<?> addEssentialFood(@RequestBody EssentialFoodRequest request,
                                              @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);

            User currentUser = userService.getUserByEmail(email);

            return ResponseEntity.ok(foodService.addEssentialFood(request, currentUser));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Searches for food items in the local database based on a query string.
     *
     * @param query The search query.
     * @return ResponseEntity containing a list of matching EssentialFood entities.
     */
    @GetMapping("/search")
    public ResponseEntity<List<EssentialFood>> searchEssentialFood(@RequestParam String query) {
        List<EssentialFood> results = foodService.searchEssentialFood(query);
        return ResponseEntity.ok(results);
    }

    /**
     * Performs a unified search, combining results from the local database and the external API.
     *
     * @param query The search query.
     * @return ResponseEntity containing a UnifiedSearchResponse DTO.
     */
    @GetMapping("/search/unified")
    public ResponseEntity<UnifiedSearchResponse> searchUnified(@RequestParam String query) {
        List<EssentialFood> localResults = foodService.searchEssentialFood(query);
        List<ApiFoodResponse> apiResults = foodService.getFoodFromApi(query);

        return ResponseEntity.ok(new UnifiedSearchResponse(localResults, apiResults));
    }
}