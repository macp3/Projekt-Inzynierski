package study.snacktrack.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import study.snacktrack.dto.TrainingDetailsResponse;
import study.snacktrack.entities.Exercise;
import study.snacktrack.entities.TrainingInfo;
import study.snacktrack.entities.User;
import study.snacktrack.services.JwtService;
import study.snacktrack.services.TrainingService;
import study.snacktrack.services.UserService;

import java.util.List;

/**
 * REST controller for handling training and exercise operations related to users,
 * including viewing available trainings, assigning/depriving user trainings,
 * and retrieving details.
 */
@RestController
@RequestMapping("/trainings")
public class TrainingController {

    /** Service for user data access and retrieval. */
    private final UserService userService;
    /** Service for JWT token handling. */
    private final JwtService jwtService;
    /** Service for training and exercise business logic. */
    private final TrainingService trainingService;

    /**
     * Constructs the TrainingController with required dependencies.
     */
    public TrainingController(UserService userService, JwtService jwtService, TrainingService trainingService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.trainingService = trainingService;
    }

    /**
     * Retrieves a list of all available training programs.
     *
     * @return ResponseEntity containing a list of TrainingInfo entities or an error message.
     */
    @GetMapping("")
    public ResponseEntity<?> getAllTrainings()
    {
        List<TrainingInfo> trainings;
        try
        {
            trainings = trainingService.getAllTrainings();
        }
        catch(IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(trainings);
    }

    /**
     * Retrieves detailed information about a specific exercise by its ID.
     *
     * @param exerciseId The ID of the exercise.
     * @return ResponseEntity containing the Exercise entity or an error message.
     */
    @GetMapping("/exercises/{exerciseId}/details")
    public ResponseEntity<?> getExerciseDetails(@PathVariable int exerciseId)
    {
        Exercise exercise;
        try
        {
            exercise = trainingService.getExerciseById(exerciseId);
        }
        catch(IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(exercise);
    }

    /**
     * Retrieves detailed information about a specific training program by its ID.
     *
     * @param id The ID of the training program.
     * @return ResponseEntity containing the TrainingDetailsResponse DTO or an error message.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTrainingDetails(@PathVariable int id)
    {
        TrainingDetailsResponse response;
        try
        {
            response = trainingService.getTrainingDetails(id);
        }
        catch(RuntimeException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the basic TrainingInfo entity currently assigned to the authenticated user.
     *
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity containing the assigned TrainingInfo or an error message if none is assigned.
     */
    @GetMapping("/my")
    public ResponseEntity<?> getUserTraining(@RequestHeader("Authorization") String authHeader)
    {
        TrainingInfo info;
        try
        {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            User user = userService.getUserByEmail(email);

            info = trainingService.getUserTraining(user.getId());
        }
        catch(IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(info);
    }

    /**
     * Retrieves detailed information (including exercises) about the training program
     * currently assigned to the authenticated user.
     *
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity containing the TrainingDetailsResponse DTO or an error message.
     */
    @GetMapping("/my/details")
    public ResponseEntity<?> getUserTrainingWithDetails(@RequestHeader("Authorization") String authHeader)
    {
        TrainingDetailsResponse response;
        try
        {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            User user = userService.getUserByEmail(email);

            TrainingInfo trainingInfo = trainingService.getUserTraining(user.getId());
            response = trainingService.getTrainingDetails(trainingInfo.getId());
        }
        catch(RuntimeException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Assigns a specific training program to the authenticated user.
     *
     * @param trainingId The ID of the training program to assign.
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity with a success message or a bad request error.
     */
    @PostMapping("/assign/{trainingId}")
    public ResponseEntity<String> assignTraining(@PathVariable int trainingId, @RequestHeader("Authorization") String authHeader)
    {
        try
        {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            User user = userService.getUserByEmail(email);

            trainingService.assignTrainingToUser(user.getId(), trainingId);
        }
        catch(IllegalArgumentException | IllegalStateException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Training successfully assigned to user");
    }

    /**
     * Removes the currently assigned training program from the authenticated user.
     *
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity with a success message or a bad request error.
     */
    @DeleteMapping("/my/deprive")
    public ResponseEntity<String> depriveTrainingFromUser(@RequestHeader("Authorization") String authHeader)
    {
        try
        {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            User user = userService.getUserByEmail(email);

            trainingService.depriveTrainingFromUser(user.getId());
        }
        catch(IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Training successfully deprived");
    }
}