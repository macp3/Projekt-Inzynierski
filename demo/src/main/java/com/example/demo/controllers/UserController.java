package com.example.demo.controllers;

import com.example.demo.entities.User;
import com.example.demo.entities.enums.Sex;
import com.example.demo.entities.enums.Status;
import com.example.demo.services.JwtService;
import com.example.demo.services.UserService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    @ResponseBody
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers()
    {
        List<User> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfileInfo(@RequestHeader("Authorization") String authHeader)
    {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    //funkcja wylacznie dla admina
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserInfo(@RequestHeader("Authorization") String authHeader)
    {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/changePassword")
    public ResponseEntity<String> changePassword(@RequestParam int userId, @RequestParam String password)
    {
        boolean success = userService.changePassword(userId, password);
        if(success)
            return ResponseEntity.ok("Password successfully changed");
        else
            return ResponseEntity.badRequest().body("Password not changed (invalid data or same as current");
    }

    @PutMapping("/{id}/parameters")
    public ResponseEntity<String> changeBodyParameters(@RequestParam int userId, @RequestParam Sex sex, @RequestParam Float height, @RequestParam Float weight, @RequestParam Integer age, @RequestParam Float dailyActivityFactor, @RequestParam Float dailyActivityTrainingFactor, @RequestParam Float weeklyWeightChangeTempo, @RequestParam Float goalWeight)
    {
        boolean success = userService.changeBodyParameters(userId, sex, height, weight, age, dailyActivityFactor, dailyActivityTrainingFactor, weeklyWeightChangeTempo, goalWeight);
        if(success)
            return ResponseEntity.ok("Body parameters successfully changed");
        else
            return ResponseEntity.badRequest().body("Body parameters not changed (invalid data");
    }

    @PutMapping("/{id}/info/streak")
    public ResponseEntity<String> updateStreak(int userId, int streak)
    {
        boolean success = userService.updateStreak(userId, streak);
        if (success)
            return ResponseEntity.ok("User's streak updated");
        else
            return ResponseEntity.badRequest().body("User's streak not updated (invalid data)");
    }

    @PutMapping("/{id}/info/status")
    public ResponseEntity<String> updateStatus(int userId, Status status)
    {
        boolean success = userService.updateStatus(userId, status);
        if (success)
            return ResponseEntity.ok("User's status updated");
        else
            return ResponseEntity.badRequest().body("User's status not updated (invalid data)");
    }

    @PutMapping("/{id}/info/expirationDate")
    public ResponseEntity<String> updateExpirationDate(int userId, LocalDate date)
    {
        boolean success = userService.updatePremiumExpiration(userId, date);
        if (success)
            return ResponseEntity.ok("User's premium expiration date updated");
        else
            return ResponseEntity.badRequest().body("User's premium expiration date not updated (specified date is before now)");
    }
}
