package study.snacktrack.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import study.snacktrack.dto.RegisteredAlimentationRequest;
import study.snacktrack.dto.RegisteredAlimentationResponse;
import study.snacktrack.entities.RegisteredAlimentation;
import study.snacktrack.entities.enums.MealNames;
import study.snacktrack.services.RegisteredAlimentationService;

import java.util.List;

/**
 * REST controller for managing users' registered food entries (alimentations)
 * and daily meal tracking.
 */
@RestController
@RequestMapping("/registered")
public class RegisteredAlimentationController {

    /** Service layer for registered alimentation business logic. */
    private final RegisteredAlimentationService service;

    /**
     * Constructs the RegisteredAlimentationController with required service dependency.
     */
    public RegisteredAlimentationController(RegisteredAlimentationService service) {
        this.service = service;
    }

    /**
     * Adds a new food entry (e.g., product or recipe) to the user's daily meal tracking.
     *
     * @param dto The RegisteredAlimentationRequest DTO.
     * @param authHeader The Authorization header for user identification.
     * @param date Optional date string for the entry (defaults to today).
     * @return ResponseEntity with a success message or a bad request error.
     */
    @PostMapping("/add")
    public ResponseEntity<String> addEntry(
            @RequestBody RegisteredAlimentationRequest dto,
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String date) {
        try {
            return ResponseEntity.ok(service.addEntry(authHeader, dto, date));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieves all registered food entries for the authenticated user for a specific day.
     *
     * @param authHeader The Authorization header for user identification.
     * @param date Optional date string to filter entries (defaults to today).
     * @return ResponseEntity containing a list of RegisteredAlimentationResponse DTOs.
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyEntries(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String date) {
        try {
            List<RegisteredAlimentationResponse> entries = service.getMyEntries(authHeader, date);
            return ResponseEntity.ok(entries);
        } catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Deletes a specific registered food entry by its ID.
     *
     * @param authHeader The Authorization header for user identification.
     * @param id The ID of the RegisteredAlimentation entry to delete.
     * @return ResponseEntity with no content (204) on success.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteEntry(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id) {
        try {
            service.deleteEntry(authHeader, id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Updates the amount or meal time of a specific registered food entry.
     *
     * @param authHeader The Authorization header for user identification.
     * @param id The ID of the RegisteredAlimentation entry to update.
     * @param dto The RegisteredAlimentationRequest DTO with updated data.
     * @return ResponseEntity containing the updated RegisteredAlimentation entity.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateEntry(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id,
            @RequestBody RegisteredAlimentationRequest dto) {
        try {
            RegisteredAlimentation updated = service.updateEntry(authHeader, id, dto);
            return ResponseEntity.ok(updated);
        } catch (ResponseStatusException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Copies all registered entries from one specific meal on a source day to
     * a target meal on a target day.
     *
     * @param authHeader The Authorization header for user identification.
     * @param fromDate The source date string.
     * @param fromMealName The meal name to copy entries from.
     * @param toDate The target date string.
     * @param toMealName The meal name to copy entries to.
     * @return ResponseEntity with a success message.
     */
    @PostMapping("/copy")
    public ResponseEntity<String> copyMeal(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String fromDate,
            @RequestParam MealNames fromMealName,
            @RequestParam String toDate,
            @RequestParam MealNames toMealName) {
        try {
            String result = service.copyMeal(authHeader, fromDate, fromMealName, toDate, toMealName);
            return ResponseEntity.ok(result);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }
}