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

class MealControllerTest {

    private MealService mealService;
    private JwtService jwtService;
    private UserService userService;
    private CommentService commentService;
    private MealController controller;

    @BeforeEach
    void setUp() {
        mealService = mock(MealService.class);
        jwtService = mock(JwtService.class);
        userService = mock(UserService.class);
        commentService = mock(CommentService.class);
        controller = new MealController(mealService, jwtService, userService, commentService);

        when(jwtService.extractEmail("token")).thenReturn("user@example.com");
        User user = new User();
        user.setId(1);
        user.setEmail("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(user);
    }

    @Test
    void createMeal_shouldReturnOk() {
        MealRequest req = new MealRequest();
        req.setName("Pizza");
        req.setDescription("Cheese pizza");
        req.setIngredients(List.of()); // minimal stub

        when(mealService.createMeal(req, 1)).thenReturn(10);

        ResponseEntity<?> response = controller.createMeal(req, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10, response.getBody());
    }

    @Test
    void editMealByUser_shouldReturnOk() {
        MealRequest req = new MealRequest();
        req.setName("Updated Pizza");

        when(mealService.editMealByUser(5, req, 1)).thenReturn("Meal updated successfully");

        ResponseEntity<String> response = controller.editMealByUser(5, req, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Meal updated successfully", response.getBody());
    }

    @Test
    void deleteMealByUser_shouldReturnOk() {
        when(mealService.deleteMealByUser(5, 1)).thenReturn("Meal successfully deleted");

        ResponseEntity<String> response = controller.deleteMealByUser(5, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Meal successfully deleted", response.getBody());
    }

    @Test
    void getMeals_shouldReturnList() {
        when(mealService.getAllMeals()).thenReturn(List.of(new Meal()));

        ResponseEntity<List<Meal>> response = controller.getMeals("Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getUserMeals_shouldReturnList() {
        when(mealService.getMealsByUser(1)).thenReturn(List.of(new Meal()));

        ResponseEntity<List<Meal>> response = controller.getUserMeals("Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void searchMealsByName_shouldReturnList() {
        when(mealService.searchMealsByName("Pizza")).thenReturn(List.of(new Meal()));

        ResponseEntity<List<Meal>> response = controller.searchMealsByName("Pizza");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

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

    @Test
    void uploadMealImage_shouldReturnOk() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(mealService.uploadMealImage(5, file, 1)).thenReturn("/images/meals/meal_5.jpg");

        ResponseEntity<String> response = controller.uploadMealImage(5, file, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("meal_5.jpg"));
    }
}
