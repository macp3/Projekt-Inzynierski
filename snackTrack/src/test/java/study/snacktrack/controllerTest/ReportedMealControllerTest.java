package study.snacktrack.controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import study.snacktrack.controllers.ReportedMealController;
import study.snacktrack.dto.ReportedMealRequest;
import study.snacktrack.dto.ReportedMealResponse;
import study.snacktrack.entities.User;
import study.snacktrack.services.JwtService;
import study.snacktrack.services.ReportedMealService;
import study.snacktrack.services.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ReportedMealController, focusing on the functionality allowing users to report inappropriate meal entries.
 * This class ensures that the controller correctly processes authenticated requests and delegates the reporting logic to the dedicated service.
 */
class ReportedMealControllerTest {

    private JwtService jwtService;
    private UserService userService;
    private ReportedMealService reportedMealService;
    private ReportedMealController controller;

    /**
     * Sets up the necessary mock services and initializes the ReportedMealController instance before each test.
     * This method also stubs the authentication process, simulating a logged-in user with ID 1 required for the reporting action.
     */
    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        userService = mock(UserService.class);
        reportedMealService = mock(ReportedMealService.class);

        controller = new ReportedMealController(jwtService, userService, reportedMealService);

        when(jwtService.extractEmail("token")).thenReturn("user@example.com");
        User user = new User();
        user.setId(1);
        user.setEmail("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(user);
    }

    /**
     * Tests the successful reporting of a meal by an authenticated user.
     * It verifies that the controller delegates the report request to the {@code ReportedMealService} and returns the generated response DTO with an HTTP 200 OK status.
     */
    @Test
    void reportMeal_shouldReturnOk() {
        ReportedMealRequest req = new ReportedMealRequest(10, "Spam meal");
        ReportedMealResponse resp = new ReportedMealResponse(1, 1, 10, "Spam meal");

        when(reportedMealService.reportMeal(10, 1, "Spam meal")).thenReturn(resp);

        ResponseEntity<?> response = controller.reportMeal(req, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ReportedMealResponse);
        assertEquals(10, ((ReportedMealResponse) response.getBody()).getMealId());
    }

    /**
     * Tests the failure case during meal reporting when a business logic exception occurs (e.g., the meal ID is invalid or the meal is already reported).
     * It verifies that the controller handles the {@code IllegalArgumentException}, returns an HTTP 400 Bad Request status, and includes the exception message in the response body.
     */
    @Test
    void reportMeal_shouldReturnBadRequest_whenException() {
        ReportedMealRequest req = new ReportedMealRequest(10, "Spam meal");

        when(reportedMealService.reportMeal(10, 1, "Spam meal"))
                .thenThrow(new IllegalArgumentException("Invalid meal"));

        ResponseEntity<?> response = controller.reportMeal(req, "Bearer token");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid meal", response.getBody());
    }
}