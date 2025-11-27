package study.snacktrack.controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import study.snacktrack.controllers.RegisteredAlimentationController;
import study.snacktrack.dto.RegisteredAlimentationRequest;
import study.snacktrack.dto.RegisteredAlimentationResponse;
import study.snacktrack.entities.RegisteredAlimentation;
import study.snacktrack.entities.enums.MealNames;
import study.snacktrack.services.RegisteredAlimentationService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the RegisteredAlimentationController, covering CRUD operations for logging food entries.
 * This class ensures that the controller correctly processes authenticated requests and delegates business logic to the {@code RegisteredAlimentationService}.
 */
class RegisteredAlimentationControllerTest {

    private RegisteredAlimentationService service;
    private RegisteredAlimentationController controller;

    /**
     * Sets up the necessary mock service and initializes the controller instance before each test.
     * This isolates the controller logic, allowing for verification of correct method calls.
     */
    @BeforeEach
    void setUp() {
        service = mock(RegisteredAlimentationService.class);
        controller = new RegisteredAlimentationController(service);
    }

    /**
     * Tests the successful addition of a new registered food entry.
     * It verifies that the controller delegates the request to the {@code RegisteredAlimentationService} and returns an HTTP 200 OK status.
     */
    @Test
    void addEntry_shouldReturnOk() {
        RegisteredAlimentationRequest req = new RegisteredAlimentationRequest();
        req.setMealName(MealNames.breakfast);
        req.setTimestamp(LocalDate.now());
        req.setAmount(100f);

        when(service.addEntry("Bearer token", req, null)).thenReturn("Meal registered");

        ResponseEntity<String> response = controller.addEntry(req, "Bearer token", null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Meal registered", response.getBody());
    }

    /**
     * Tests the failure case during entry addition when a business logic exception occurs (e.g., validation error).
     * It verifies that the controller catches the exception, returns an HTTP 400 Bad Request status, and includes the error message.
     */
    @Test
    void addEntry_shouldReturnBadRequest_whenException() {
        RegisteredAlimentationRequest req = new RegisteredAlimentationRequest();
        req.setMealName(MealNames.breakfast);

        when(service.addEntry("Bearer token", req, null))
                .thenThrow(new IllegalArgumentException("Invalid"));

        ResponseEntity<String> response = controller.addEntry(req, "Bearer token", null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid", response.getBody());
    }

    /**
     * Tests the successful retrieval of a user's food entries for a specific date or period.
     * It verifies that the controller delegates the fetch request to the {@code RegisteredAlimentationService} and returns a list of DTOs with an HTTP 200 OK status.
     */
    @Test
    void getMyEntries_shouldReturnList() {
        RegisteredAlimentationResponse dto = new RegisteredAlimentationResponse();
        dto.setMealName(MealNames.lunch);

        when(service.getMyEntries("Bearer token", null)).thenReturn(List.of(dto));

        ResponseEntity<?> response = controller.getMyEntries("Bearer token", null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> body = (List<?>) response.getBody();
        assertEquals(1, body.size());
    }

    /**
     * Tests the successful deletion of a registered food entry by ID.
     * It verifies that the controller delegates the delete operation and returns an HTTP 204 No Content status, which is typical for successful deletion without a body.
     */
    @Test
    void deleteEntry_shouldReturnNoContent() {
        doNothing().when(service).deleteEntry("Bearer token", 5);

        ResponseEntity<String> response = controller.deleteEntry("Bearer token", 5);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    /**
     * Tests the successful update of an existing food entry.
     * It verifies that the controller delegates the update request and returns the updated {@code RegisteredAlimentation} entity with an HTTP 200 OK status.
     */
    @Test
    void updateEntry_shouldReturnOk() {
        RegisteredAlimentationRequest req = new RegisteredAlimentationRequest();
        req.setMealName(MealNames.dinner);
        req.setAmount(200f);

        RegisteredAlimentation updated = new RegisteredAlimentation();
        updated.setId(5);
        updated.setMealName(MealNames.dinner);

        when(service.updateEntry("Bearer token", 5, req)).thenReturn(updated);

        ResponseEntity<?> response = controller.updateEntry("Bearer token", 5, req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updated, response.getBody());
    }

    /**
     * Tests the functionality for copying a meal entry from one meal type/day to another.
     * It verifies that the controller delegates the complex copy operation and returns a success message with an HTTP 200 OK status.
     */
    @Test
    void copyMeal_shouldReturnOk() {
        when(service.copyMeal("Bearer token", "2025-11-25", MealNames.breakfast, "2025-11-26", MealNames.lunch))
                .thenReturn("Meal copied successfully");

        ResponseEntity<String> response = controller.copyMeal(
                "Bearer token", "2025-11-25", MealNames.breakfast, "2025-11-26", MealNames.lunch);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Meal copied successfully", response.getBody());
    }
}