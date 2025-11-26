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

class RegisteredAlimentationControllerTest {

    private RegisteredAlimentationService service;
    private RegisteredAlimentationController controller;

    @BeforeEach
    void setUp() {
        service = mock(RegisteredAlimentationService.class);
        controller = new RegisteredAlimentationController(service);
    }

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

    @Test
    void deleteEntry_shouldReturnNoContent() {
        doNothing().when(service).deleteEntry("Bearer token", 5);

        ResponseEntity<String> response = controller.deleteEntry("Bearer token", 5);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

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
