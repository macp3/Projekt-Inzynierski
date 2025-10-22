package study.snacktrack.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import study.snacktrack.dto.RegisteredAlimentationRequest;
import study.snacktrack.dto.RegisteredAlimentationResponse;
import study.snacktrack.entities.RegisteredAlimentation;
import study.snacktrack.services.RegisteredAlimentationService;

import java.util.List;

@RestController
@RequestMapping("/registered")
public class RegisteredAlimentationController {

    private final RegisteredAlimentationService service;

    public RegisteredAlimentationController(RegisteredAlimentationService service) {
        this.service = service;
    }

    // dziala
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

    // dziala
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

    // dziala
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

    // dziala
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
}
