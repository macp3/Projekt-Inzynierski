package study.snacktrack.controllers;

import java.util.List;

import study.snacktrack.dto.TrainingDetailsResponse;
import study.snacktrack.services.JwtService;
import study.snacktrack.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import study.snacktrack.entities.Exercise;
import study.snacktrack.entities.TrainingInfo;
import study.snacktrack.entities.User;
import study.snacktrack.services.TrainingService;

@RestController
@RequestMapping("/trainings")
public class TrainingController {

    private final UserService userService;
    private final JwtService jwtService;
    private final TrainingService trainingService;

    public TrainingController(UserService userService, JwtService jwtService, TrainingService trainingService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.trainingService = trainingService;
    }

    //dziala
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

    //dziala
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

    //dziala
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

    //zrobilem assign i potem wywolalem to i nie dziala
    //juz dziala
    //dziala nawet po usunieciu treningu z bazy
    //dziala
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

    //dziala
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

    //dziala
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

    //dziala
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
