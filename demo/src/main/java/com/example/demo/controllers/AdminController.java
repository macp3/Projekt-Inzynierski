package com.example.demo.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AddExerciseToTrainingRequest;
import com.example.demo.dto.ExerciseRequest;
import com.example.demo.dto.NotificationRequest;
import com.example.demo.dto.TrainingDetailsResponse;
import com.example.demo.dto.TrainingRequest;
import com.example.demo.entities.Admin;
import com.example.demo.entities.Exercise;
import com.example.demo.entities.Notification;
import com.example.demo.entities.ReportedComment;
import com.example.demo.entities.ReportedMeal;
import com.example.demo.entities.Training;
import com.example.demo.entities.TrainingInfo;
import com.example.demo.entities.User;
import com.example.demo.entities.enums.Recipients;
import com.example.demo.repositories.AdminRepository;
import com.example.demo.services.CommentService;
import com.example.demo.services.FoodService;
import com.example.demo.services.JwtService;
import com.example.demo.services.MealService;
import com.example.demo.services.NotificationService;
import com.example.demo.services.ReportedCommentService;
import com.example.demo.services.ReportedMealService;
import com.example.demo.services.TrainingService;
import com.example.demo.services.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminRepository adminRepository;
    private final UserService userService;
    private final ReportedMealService reportedMealService;
    private final ReportedCommentService reportedCommentService;
    private final JwtService jwtService;
    private final TrainingService trainingService;
    private final NotificationService notificationService;

    public AdminController(AdminRepository adminRepository, UserService userService, MealService mealService, CommentService commentService, ReportedMealService reportedMealService, ReportedCommentService reportedCommentService, FoodService foodService, JwtService jwtService, TrainingService trainingService, NotificationService notificationService) {
        this.adminRepository = adminRepository;
        this.userService = userService;
        this.reportedMealService = reportedMealService;
        this.reportedCommentService = reportedCommentService;
        this.jwtService = jwtService;
        this.trainingService = trainingService;
        this.notificationService = notificationService;
    }

    @GetMapping("/dashboard")
    public String getDashboard() {
        return "Witaj, ADMIN!";
    }

    //nowe
    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUserInfo(@PathVariable int userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    //doi naprawy - updatowanie streaka: albo +1 albo wyzerowac
    /*@GetMapping("/{userId}/updateStreak")
    public ResponseEntity<User>updateStreak(@PathVariable int userId, @RequestParam int streak)
    {

        User user = userService.getUserById(userId);

        user.setStreak(streak);
        userService.updateStreak(userId, streak);
        return ResponseEntity.ok(user);
    }*/
    //admin
    @PutMapping("/user/{userId}/info/expirationDate")
    public ResponseEntity<User> updateExpirationDate(int userId, @RequestParam LocalDate date) {
        User user = userService.getUserById(userId);
        userService.updatePremiumExpiration(user.getId(), date);
        return ResponseEntity.ok(user);
    }

    //admin
    @ResponseBody
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    //admin
    @GetMapping("reports/meals/user/{userId}")
    public ResponseEntity<List<ReportedMeal>> getAllMealReportsByUser(@PathVariable int userId) {
        List<ReportedMeal> reports = reportedMealService.getAllReportsByUser(userId);
        return ResponseEntity.ok(reports);
    }

    //admin
    @GetMapping("reports/comments/user/{userId}")
    public ResponseEntity<List<ReportedComment>> getAllCommentReportsByUser(@PathVariable int userId) {
        List<ReportedComment> reports = reportedCommentService.getAllReportsByUser(userId);

        return ResponseEntity.ok(reports);
    }

    @PostMapping("/trainings/add")
    public ResponseEntity<String> addTraining(@RequestBody TrainingRequest request) {
        trainingService.createTraining(request, 1);
        return ResponseEntity.ok("Training added successfully");
    }

    //dodawanie cwiczenia do treningu
    @PostMapping("/trainings/addExercise")
    public ResponseEntity<TrainingDetailsResponse> addExerciseToTraining(@RequestBody AddExerciseToTrainingRequest request, @RequestHeader("Authorization") String authHeader) {
        //walidacja admina
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        Admin admin = adminRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        TrainingDetailsResponse response = trainingService.addExerciseToTraining(
                request.getExerciseId(),
                request.getTrainingId(),
                admin.getId(),
                request.getDayOfExercise()
        );
        return ResponseEntity.ok(response);
    }

    ////////////////////////////////////////////////////////////////////////

    @GetMapping("/trainings")
    public ResponseEntity<List<TrainingInfo>> getAllTrainings() {
        return ResponseEntity.ok(trainingService.getAllTrainings());
    }

    @GetMapping("/trainings/{trainingId}/details")
    public ResponseEntity<Training> getTrainingDetails(@PathVariable int trainingId) {
        return ResponseEntity.ok(trainingService.getTrainingById(trainingId));
    }

    @GetMapping("/exercises")
    public ResponseEntity<List<Exercise>> getAllExercises() {
        return ResponseEntity.ok(trainingService.getAllExercises());
    }

    @GetMapping("/exercises/{exerciseId}/details")
    public ResponseEntity<Exercise> getExerciseDetails(@PathVariable int exerciseId) {
        return ResponseEntity.ok(trainingService.getExerciseById(exerciseId));
    }

    @PostMapping("/exercises/add")
    public ResponseEntity<Exercise> addExercise(@RequestBody ExerciseRequest request) {
        Exercise exercise = trainingService.createExercise(request.getName(), request.getDescription(), request.getType(), request.getDifficulty(), request.getNumberOfSets(), request.getRepetitionsPerSet());
        return ResponseEntity.ok(exercise);
    }

    @PostMapping("/notifications/add")
    public ResponseEntity<Notification> createNotification(
            @RequestBody NotificationRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        Notification notification = notificationService.createNotification(request, admin);
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getAllNotifications(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        adminRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/notifications/filter")
    public ResponseEntity<List<Notification>> getNotificationsByRecipients(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Recipients recipients
    ) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        adminRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        List<Notification> notifications = notificationService.getNotificationsByRecipients(recipients);
        return ResponseEntity.ok(notifications);
    }
}
