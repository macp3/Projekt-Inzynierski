package study.snacktrack.controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import study.snacktrack.controllers.FoodController;
import study.snacktrack.dto.EssentialFoodRequest;
import study.snacktrack.dto.EssentialFoodResponse;
import study.snacktrack.dto.ApiFoodResponse;
import study.snacktrack.dto.UnifiedSearchResponse;
import study.snacktrack.entities.EssentialFood;
import study.snacktrack.entities.User;
import study.snacktrack.services.FoodService;
import study.snacktrack.services.JwtService;
import study.snacktrack.services.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the FoodController, covering operations related to essential (user-defined) foods and external API food sources.
 * This class ensures that the controller correctly handles requests, interacts with necessary services, and returns appropriate HTTP responses.
 */
class FoodControllerTest {

    private FoodService foodService;
    private UserService userService;
    private JwtService jwtService;
    private FoodController controller;

    /**
     * Sets up mock services and initializes the FoodController instance before each test execution.
     * This ensures test isolation by controlling the behavior of all external dependencies.
     */
    @BeforeEach
    void setUp() {
        foodService = mock(FoodService.class);
        userService = mock(UserService.class);
        jwtService = mock(JwtService.class);
        controller = new FoodController(foodService, userService, jwtService);
    }

    /**
     * Tests the successful retrieval of all essential food items from the local database.
     * It verifies that the controller delegates to the {@code FoodService} and returns the expected list of responses.
     */
    @Test
    void getAllEssentials_shouldReturnList() {
        EssentialFood food = new EssentialFood();
        food.setName("Apple");
        food.setCalories(50);
        food.setDescription("Fresh apple");

        when(foodService.getAllEssentials())
                .thenReturn(List.of(new EssentialFoodResponse(food)));

        List<EssentialFoodResponse> result = controller.getAllEssentials();

        assertEquals(1, result.size());
        assertEquals("Apple", result.get(0).getName());
    }


    /**
     * Tests the retrieval of food items from the external API based on a search query.
     * It verifies that the controller delegates the search request to the {@code FoodService} and returns the list of API-specific DTOs.
     */
    @Test
    void getFoodFromApi_shouldReturnList() {
        when(foodService.getFoodFromApi("apple")).thenReturn(List.of(new ApiFoodResponse()));

        List<ApiFoodResponse> result = controller.getFoodFromApi("apple");

        assertEquals(1, result.size());
    }


    /**
     * Tests the failure case when attempting to retrieve food details from the external API by ID and an exception occurs (e.g., resource not found).
     * It verifies that the controller catches the exception, returns an HTTP 400 Bad Request status, and includes the error message in the response body.
     */
    @Test
    void getFoodFromApiById_shouldReturnBadRequest_whenException() {
        when(foodService.getFoodFromApiById(5)).thenThrow(new RuntimeException("Not found"));

        ResponseEntity<?> response = controller.getFoodFromApiById(5);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Not found", response.getBody());
    }

    /**
     * Tests the successful addition of a new essential food item by an authenticated user.
     * It verifies that the user is correctly identified via JWT, the request is processed by the {@code FoodService}, and an HTTP 200 OK status is returned.
     */
    @Test
    void addEssentialFood_shouldReturnOk() {
        EssentialFoodRequest req = new EssentialFoodRequest(
                "Apple", "Fresh apple", 50, 1, 0, 10, "g", 100, "BrandX"
        );

        User user = new User();
        user.setId(1);

        when(jwtService.extractEmail("token")).thenReturn("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(user);
        when(foodService.addEssentialFood(req, user)).thenReturn("Food has been added to database");

        ResponseEntity<?> response = controller.addEssentialFood(req, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Food has been added to database", response.getBody());
    }


    /**
     * Tests the failure case during the addition of an essential food item when a validation exception occurs in the service layer.
     * It verifies that the controller catches the {@code IllegalArgumentException}, returns an HTTP 400 Bad Request, and displays the exception message.
     */
    @Test
    void addEssentialFood_shouldReturnBadRequest_whenException() {
        EssentialFoodRequest req = new EssentialFoodRequest(
                "Apple", "Fresh apple", 50, 1, 0, 10, "g", 100, "BrandX"
        );

        User user = new User();
        user.setId(1);

        when(jwtService.extractEmail("token")).thenReturn("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(user);
        when(foodService.addEssentialFood(req, user))
                .thenThrow(new IllegalArgumentException("Invalid"));

        ResponseEntity<?> response = controller.addEssentialFood(req, "Bearer token");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid", response.getBody());
    }


    /**
     * Tests the search functionality for locally defined essential food items.
     * It verifies that the controller correctly delegates the search query and returns the matching list of {@code EssentialFood} entities.
     */
    @Test
    void searchEssentialFood_shouldReturnList() {
        EssentialFood food = new EssentialFood();
        food.setName("Banana");
        food.setCalories(100);

        when(foodService.searchEssentialFood("banana"))
                .thenReturn(List.of(food));

        ResponseEntity<List<EssentialFood>> response = controller.searchEssentialFood("banana");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Banana", response.getBody().get(0).getName());
    }


    /**
     * Tests the unified search endpoint, which combines results from local essential foods and the external API.
     * It verifies that the controller delegates both searches and correctly aggregates the results into a single {@code UnifiedSearchResponse} DTO.
     */
    @Test
    void searchUnified_shouldReturnUnifiedResponse() {
        EssentialFood food = new EssentialFood();
        food.setName("Orange");
        food.setCalories(80);

        when(foodService.searchEssentialFood("orange"))
                .thenReturn(List.of(food));
        when(foodService.getFoodFromApi("orange"))
                .thenReturn(List.of());

        ResponseEntity<UnifiedSearchResponse> response = controller.searchUnified("orange");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getLocalResults().size());
        assertEquals(0, response.getBody().getApiResults().size());
    }

}