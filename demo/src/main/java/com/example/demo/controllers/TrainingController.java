package com.example.demo.controllers;

import com.example.demo.dto.AssignTrainingRequest;
import com.example.demo.dto.TrainingDetailsResponse;
import com.example.demo.dto.TrainingRequest;
import com.example.demo.entities.*;
import com.example.demo.services.JwtService;
import com.example.demo.services.TrainingService;
import com.example.demo.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainings")
public class TrainingController
{
    private final UserService userService;
    private final JwtService jwtService;
    private final TrainingService trainingService;

    public TrainingController(UserService userService, JwtService jwtService, TrainingService trainingService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.trainingService = trainingService;
    }

    @GetMapping("")
    public ResponseEntity<List<TrainingInfo>> getAllTrainings()
    {
        return ResponseEntity.ok(trainingService.getAllTrainings());
    }

    @GetMapping("/exercises/{exerciseId}/details")
    public ResponseEntity<Exercise> getExerciseDetails(@PathVariable int exerciseId)
    {
        return ResponseEntity.ok(trainingService.getExerciseById(exerciseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingDetailsResponse> getTrainingDetails(@PathVariable int id) {
        TrainingDetailsResponse response = trainingService.getTrainingDetails(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/assign/{trainingId}")
    public ResponseEntity<String> assignTraining(@PathVariable int trainingId, @RequestHeader("Authorization") String authHeader)
    {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        User user = userService.getUserByEmail(email);

        trainingService.assignTrainingToUser(user.getId(), trainingId);
        return ResponseEntity.ok("Training successfully assigned to user");
    }

    @DeleteMapping("/my/deprive")
    public ResponseEntity<String> depriveTrainingFromUser(@RequestHeader("Authorization") String authHeader)
    {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        User user = userService.getUserByEmail(email);

        trainingService.depriveTrainingFromUser(user.getId());
        return ResponseEntity.ok("Training successfully deprived");
    }

    @GetMapping("/my")
    public ResponseEntity<TrainingInfo> getUserTraining(@RequestHeader("Authorization") String authHeader)
    {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        User user = userService.getUserByEmail(email);

        TrainingInfo training = trainingService.getUserTraining(user.getId());
        return ResponseEntity.ok(training);
    }
}
