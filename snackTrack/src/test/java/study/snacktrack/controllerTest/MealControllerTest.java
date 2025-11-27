package study.snacktrack.controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import study.snacktrack.controllers.MealController;
import study.snacktrack.dto.MealRequest;
import study.snacktrack.dto.MealResponse;
import study.snacktrack.entities.Meal;
import study.snacktrack.entities.User;
import study.snacktrack.services.CommentService;
import study.snacktrack.services.JwtService;
import study.snacktrack.services.MealService;
import study.snacktrack.services.UserService;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the MealController, covering CRUD operations related to meals, including creation, editing, deletion, and image upload.
 * This class uses Mockito to isolate the controller logic, verifying correct delegation of business logic to the {@code MealService}.
 */
class MealControllerTest {

    private MealService mealService;
    private JwtService jwtService;
    private UserService userService;
    private CommentService commentService;
    private MealController controller;

    /**
     * Sets up the mock services and initializes the MealController instance before each test.
     * This method also sets up the mock authentication context, simulating a logged-in user with ID 1.
     */
    @BeforeEach
    void setUp() {
        mealService = mock(MealService.class);
        jwtService = mock(JwtService.class);
        userService = mock(UserService.class);
        commentService = mock(CommentService.class);
        controller = new MealController(mealService, jwtService, userService);

        when(jwtService.extractEmail("token")).thenReturn("user@example.com");
        User user = new User();
        user.setId(1);
        user.setEmail("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(user);
    }

    /**
     * Tests the successful creation of a new meal.
     * It verifies that the controller delegates the request to the {@code MealService} and returns the generated meal ID with an HTTP 200 OK status.
     */
    @Test
    void createMeal_shouldReturnOk() {
        MealRequest req = new MealRequest();
        req.setName("Pizza");
        req.setDescription("Cheese pizza");
        req.setIngredients(List.of());

        when(mealService.createMeal(req, 1)).thenReturn(10);

        ResponseEntity<?> response = controller.createMeal(req, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10, response.getBody());
    }

    /**
     * Tests the successful editing of an existing meal by its author.
     * It verifies that the controller delegates the edit request to the {@code MealService} and returns a success message with an HTTP 200 OK status.
     */
    @Test
    void editMealByUser_shouldReturnOk() {
        MealRequest req = new MealRequest();
        req.setName("Updated Pizza");

        when(mealService.editMealByUser(5, req, 1)).thenReturn("Meal updated successfully");

        ResponseEntity<String> response = controller.editMealByUser(5, req, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Meal updated successfully", response.getBody());
    }

    /**
     * Tests the successful deletion of a meal by its author.
     * It verifies that the controller delegates the delete operation to the {@code MealService} and returns a success message with an HTTP 200 OK status.
     */
    @Test
    void deleteMealByUser_shouldReturnOk() {
        when(mealService.deleteMealByUser(5, 1)).thenReturn("Meal successfully deleted");

        ResponseEntity<String> response = controller.deleteMealByUser(5, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Meal successfully deleted", response.getBody());
    }

    /**
     * Tests the retrieval of all publicly available meals.
     * It verifies that the controller delegates to the {@code MealService} and returns a list of {@code Meal} entities with an HTTP 200 OK status.
     */
    @Test
    void getMeals_shouldReturnList() {
        when(mealService.getAllMeals()).thenReturn(List.of(new Meal()));

        ResponseEntity<List<Meal>> response = controller.getMeals("Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    /**
     * Tests the retrieval of all meals created by the currently authenticated user.
     * It verifies that the controller correctly uses the authenticated user's ID to fetch the meals.
     */
    @Test
    void getUserMeals_shouldReturnList() {
        when(mealService.getMealsByUser(1)).thenReturn(List.of(new Meal()));

        ResponseEntity<List<Meal>> response = controller.getUserMeals("Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    /**
     * Tests the search functionality for meals by their name.
     * It verifies that the controller delegates the search query to the {@code MealService} and returns the list of matching {@code Meal} entities.
     */
    @Test
    void searchMealsByName_shouldReturnList() {
        when(mealService.searchMealsByName("Pizza")).thenReturn(List.of(new Meal()));

        ResponseEntity<List<Meal>> response = controller.searchMealsByName("Pizza");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    /**
     * Tests the retrieval of detailed information for a specific meal, including its ingredients.
     * It verifies that the controller delegates to the {@code MealService} and returns the complex {@code MealResponse} DTO.
     */
    @Test
    void getMealDetails_shouldReturnOk() {
        MealResponse mealResponse = new MealResponse();
        mealResponse.setId(5);
        mealResponse.setName("Pizza");

        when(mealService.getMealWithIngredients(5)).thenReturn(mealResponse);

        ResponseEntity<?> response = controller.getMealDetails(5, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Pizza", ((MealResponse) response.getBody()).getName());
    }

    /**
     * Tests the successful upload of an image for a specific meal.
     * It verifies that the controller handles the {@code MultipartFile} and delegates the upload to the {@code MealService}, returning the image path.
     * @throws IOException if the mocked file operation fails unexpectedly
     */
    @Test
    void uploadMealImage_shouldReturnOk() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(mealService.uploadMealImage(5, file, 1)).thenReturn("/images/meals/meal_5.jpg");

        ResponseEntity<String> response = controller.uploadMealImage(5, file, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("meal_5.jpg"));
    }
}