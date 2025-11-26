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

class ReportedMealControllerTest {

    private JwtService jwtService;
    private UserService userService;
    private ReportedMealService reportedMealService;
    private ReportedMealController controller;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        userService = mock(UserService.class);
        reportedMealService = mock(ReportedMealService.class);

        // MealService i CommentService nie są używane w kontrolerze, więc przekazujemy null
        controller = new ReportedMealController(null, jwtService, userService, null, reportedMealService);

        // Stub autoryzacji
        when(jwtService.extractEmail("token")).thenReturn("user@example.com");
        User user = new User();
        user.setId(1);
        user.setEmail("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(user);
    }

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
