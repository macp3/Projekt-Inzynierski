package study.snacktrack.controllers;

import study.snacktrack.dto.LoginRequest;
import study.snacktrack.entities.Admin;
import study.snacktrack.repositories.AdminRepository;
import study.snacktrack.services.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * REST controller for handling administrative authentication processes.
 * Provides endpoints for administrative login.
 */
@RestController
@RequestMapping("/admin-auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Handles the administrator login request.
     * Authenticates the admin using email and password, and returns a JWT token upon success.
     *
     * @param request The LoginRequest containing the admin's email and password.
     * @return ResponseEntity containing the JWT token if successful, or UNAUTHORIZED status.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        String token = jwtService.generateToken(admin.getEmail(), "ADMIN");
        return ResponseEntity.ok(token);
    }
}
