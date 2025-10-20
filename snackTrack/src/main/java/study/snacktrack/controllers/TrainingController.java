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
    public ResponseEntity<List<TrainingInfo>> getAllTrainings() {
        return ResponseEntity.ok(trainingService.getAllTrainings());
    }

    //dziala
    @GetMapping("/exercises/{exerciseId}/details")
    public ResponseEntity<Exercise> getExerciseDetails(@PathVariable int exerciseId) {
        return ResponseEntity.ok(trainingService.getExerciseById(exerciseId));
    }

    //dziala
    @GetMapping("/{id}")
    public ResponseEntity<TrainingDetailsResponse> getTrainingDetails(@PathVariable int id) {
        TrainingDetailsResponse response = trainingService.getTrainingDetails(id);
        return ResponseEntity.ok(response);
    }

    //zrobilem assign i potem wywolalem to i nie dziala
    //juz dziala
    //dziala nawet po usunieciu treningu z bazy
    //DZIALAAAAAAA
    @GetMapping("/my")
    public ResponseEntity<TrainingInfo> getUserTraining(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        User user = userService.getUserByEmail(email);

        TrainingInfo trainingInfo = trainingService.getUserTraining(user.getId());
        return ResponseEntity.ok(trainingInfo);
    }

    //dziala
    @GetMapping("/my/details")
    public ResponseEntity<TrainingDetailsResponse> getUserTrainingWithDetails(@RequestHeader("Authorization") String authHeader)
    {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        User user = userService.getUserByEmail(email);

        TrainingInfo trainingInfo = trainingService.getUserTraining(user.getId());
        return ResponseEntity.ok(trainingService.getTrainingDetails(trainingInfo.getId()));
    }

    //dziala
    @PostMapping("/assign/{trainingId}")
    public ResponseEntity<String> assignTraining(@PathVariable int trainingId, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        User user = userService.getUserByEmail(email);

        trainingService.assignTrainingToUser(user.getId(), trainingId);
        return ResponseEntity.ok("Training successfully assigned to user");
    }

    //dziala
    @DeleteMapping("/my/deprive")
    public ResponseEntity<String> depriveTrainingFromUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        User user = userService.getUserByEmail(email);

        trainingService.depriveTrainingFromUser(user.getId());
        return ResponseEntity.ok("Training successfully deprived");
    }
}
