package com.example.demo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dto.BodyParametersRequest;
import com.example.demo.dto.BodyParametersResponse;
import com.example.demo.entities.BodyParameters;
import com.example.demo.entities.User;
import com.example.demo.entities.enums.DietTypes;
import com.example.demo.services.JwtService;
import com.example.demo.services.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    //admin
    @ResponseBody
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfileInfo(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    //funkcja wylacznie dla admina
    /*@GetMapping("/{id}")
    public ResponseEntity<User> getUserInfo(@RequestHeader("Authorization") String authHeader)
    {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }*/
    @PutMapping("/changePassword")
    public ResponseEntity<User> changePassword(@RequestHeader("Authorization") String authHeader, @RequestParam String password) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email);
        userService.changePassword(user.getId(), password);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/changeParameters")
    public ResponseEntity<BodyParametersResponse> changeBodyParameters(@RequestHeader("Authorization") String authHeader, BodyParametersRequest request) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email);

        BodyParametersResponse response
                = userService.changeBodyParameters(user.getId(), request.getSex(), request.getHeight(), request.getWeight(), request.getAge(), request.getDailyActivityFactor(), request.getDailyActivityTrainingFactor(), request.getWeeklyWeightChangeTempo(), request.getGoalWeight());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/changePrefferedDiet")
    public ResponseEntity<User> changePrefferedDiet(@RequestHeader("Authorization") String authHeader, DietTypes prefferedDiet) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email);

        userService.changePrefferedDiet(user.getId(), prefferedDiet);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/addParameters")
    public ResponseEntity<BodyParameters> addBodyParameters(@RequestHeader("Authorization") String authHeader, @RequestBody BodyParametersRequest request) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email);

        BodyParameters response
                = userService.addBodyParameters(user.getId(), request.getSex(), request.getHeight(), request.getWeight(), request.getAge(), request.getDailyActivityFactor(), request.getDailyActivityTrainingFactor(), request.getWeeklyWeightChangeTempo(), request.getGoalWeight());
        return ResponseEntity.ok(response);
    }

}
