package study.snacktrack.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import study.snacktrack.dto.*;
import study.snacktrack.entities.*;
import study.snacktrack.entities.enums.Recipients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import study.snacktrack.repositories.AdminRepository;
import study.snacktrack.services.CommentService;
import study.snacktrack.services.FoodService;
import study.snacktrack.services.JwtService;
import study.snacktrack.services.MealService;
import study.snacktrack.services.NotificationService;
import study.snacktrack.services.ReportedCommentService;
import study.snacktrack.services.ReportedMealService;
import study.snacktrack.services.TrainingService;
import study.snacktrack.services.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminRepository adminRepository;
    private final UserService userService;
    private final ReportedMealService reportedMealService;
    private final ReportedCommentService reportedCommentService;
    private final CommentService commentService;
    private final JwtService jwtService;
    private final TrainingService trainingService;
    private final NotificationService notificationService;

    public AdminController(AdminRepository adminRepository, UserService userService, MealService mealService,
                           CommentService commentService, ReportedMealService reportedMealService,
                           ReportedCommentService reportedCommentService, FoodService foodService, JwtService jwtService,
                           TrainingService trainingService, NotificationService notificationService) {
        this.adminRepository = adminRepository;
        this.userService = userService;
        this.reportedMealService = reportedMealService;
        this.reportedCommentService = reportedCommentService;
        this.jwtService = jwtService;
        this.trainingService = trainingService;
        this.notificationService = notificationService;
        this.commentService = commentService;
    }

    // dziala
    @GetMapping("/dashboard")
    public String getDashboard() {
        return "Witaj, ADMIN!";
    }

    // nowe
    // dziala
    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUserInfo(@PathVariable int userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    //dziala
    @PutMapping("/user/{userId}/info/expirationDate")
    public ResponseEntity<User> updateExpirationDate(@PathVariable int userId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
    {
        User user = userService.updatePremiumExpiration(userId, date);
        return ResponseEntity.ok(user);
    }

    // admin
    // dziala
    @ResponseBody
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    // admin
    // dziala
    @GetMapping("/reports/meals/user/{userId}")
    public ResponseEntity<List<ReportedMeal>> getAllMealReportsByUser(@PathVariable int userId) {
        List<ReportedMeal> reports = reportedMealService.getAllReportsByUser(userId);
        return ResponseEntity.ok(reports);
    }

    // admin
    //dziala
    @GetMapping("/reports/comments/user/{userId}")
    public ResponseEntity<List<ReportedComment>> getAllCommentReportsByUser(@PathVariable int userId) {
        List<ReportedComment> reports = reportedCommentService.getAllReportsByUser(userId);

        return ResponseEntity.ok(reports);
    }

    ////////////////////////////////////////////////////////////////////////

    // dziala
    @GetMapping("/trainings")
    public ResponseEntity<List<TrainingInfo>> getAllTrainings() {
        return ResponseEntity.ok(trainingService.getAllTrainings());
    }

    //dziala
    @GetMapping("/trainings/{trainingId}/details")
    public ResponseEntity<TrainingDetailsResponse> getTrainingDetails(@PathVariable int trainingId) {
        return ResponseEntity.ok(trainingService.getTrainingDetails(trainingId));
    }

    //DZIALAAAAAAAAAAAA
    @PostMapping("/trainings/add")
    public ResponseEntity<String> addTraining(@RequestBody TrainingRequest request, @RequestHeader("Authorization") String authHeader)
    {
        // walidacja admina
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        trainingService.createTraining(request, admin.getId());
        return ResponseEntity.ok("Training added successfully");
    }

    //dzialaaaaaaaaaaaaaa
    @PutMapping("/trainings/{trainingId}/edit")
    public ResponseEntity<String> editTraining(@PathVariable int trainingId, @RequestBody TrainingRequest request,
            @RequestHeader("Authorization") String authHeader) {
        // walidacja admina
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        trainingService.editTraining(request, admin.getId(), trainingId);
        return ResponseEntity.ok("Training edited successfully");
    }

    // DZIALAAAAAAAAAAAAA
    @PostMapping("/trainings/addExercise")
    public ResponseEntity<TrainingDetailsResponse> addExerciseToTraining(
            @RequestBody AddExerciseToTrainingRequest request, @RequestHeader("Authorization") String authHeader) {
        // walidacja admina
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        TrainingDetailsResponse response = trainingService.addExerciseToTraining(
                request.getExerciseId(),
                request.getTrainingId(),
                admin.getId(),
                request.getDayOfExercise());
        return ResponseEntity.ok(response);
    }

    //dziala
    @DeleteMapping("/trainings/{trainingId}/delete/{exerciseId}")
    public ResponseEntity<TrainingDetailsResponse> deleteExercisesByIdFromTraining(@PathVariable int trainingId,
            @PathVariable int exerciseId) {
        return ResponseEntity.ok(trainingService.deleteAllExercisesByIdFromTraining(trainingId, exerciseId));
    }

    // dziala
    @DeleteMapping("/trainings/{trainingId}/delete/{exerciseId}/{dayOfExercise}")
    public ResponseEntity<TrainingDetailsResponse> deleteExerciseByIdAndDayFromTraining(@PathVariable int trainingId,
            @PathVariable int exerciseId, @PathVariable int dayOfExercise) {
        return ResponseEntity
                .ok(trainingService.deleteExerciseByIdAndDayFromTraining(trainingId, exerciseId, dayOfExercise));
    }

    // dziala
    @DeleteMapping("/trainings/{trainingId}/delete")
    public ResponseEntity<String> deleteTraining(@PathVariable int trainingId) {
        trainingService.deleteTraining(trainingId);
        return ResponseEntity.ok("Training deleted successfully");
    }

    // dziala
    @GetMapping("/exercises")
    public ResponseEntity<List<Exercise>> getAllExercises() {
        return ResponseEntity.ok(trainingService.getAllExercises());
    }

    // dziala
    @GetMapping("/exercises/{exerciseId}/details")
    public ResponseEntity<Exercise> getExerciseDetails(@PathVariable int exerciseId) {
        return ResponseEntity.ok(trainingService.getExerciseById(exerciseId));
    }

    //dziala
    @PostMapping("/exercises/add")
    public ResponseEntity<Exercise> addExercise(@RequestBody ExerciseRequest request) {
        Exercise exercise = trainingService.createExercise(request.getName(), request.getDescription(),
                request.getType(), request.getDifficulty(), request.getNumberOfSets(), request.getRepetitionsPerSet());
        return ResponseEntity.ok(exercise);
    }

    // dziala
    @DeleteMapping("/exercises/delete/{exerciseId}")
    public ResponseEntity<String> deleteExercise(@PathVariable int exerciseId) {
        trainingService.deleteExercise(exerciseId);
        return ResponseEntity.ok("Exercise deleted successfully");
    }

    ////////////////////////////////////////////////////////////////////

    // login, email, password xd
    // walidacja stringow - nic nie wyrzuca exeption
    // porownanie do nulli tak samo
    // Maciej: zrobione
    //dziala, ale dziwne te exeption troche bo wyskakuje takie cos jak recipients "brak" a widze ze w metodzie fajnie wyrzuciles wiadomosc wec nwm co tu sie zadzialo:
    //2025-10-20T14:22:22.218Z  WARN 1 --- [snackTrack] [nio-8080-exec-8] .w.s.m.s.DefaultHandlerExceptionResolver : Resolved [org.springframework.http.converter.HttpMessageNotReadableException: JSON parse error: Cannot deserialize value of type `study.snacktrack.entities.enums.Recipients` from String "brak": not one of the values accepted for Enum class: [all, non_premium, premium]]
    @PostMapping("/notifications/add")
    public ResponseEntity<String> createNotification(
            @RequestBody NotificationRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        notificationService.createNotification(request, admin);
        return ResponseEntity.ok("Notification created successfully");
    }

    // pola nie moga byc nullami
    // MAciej: już nie będą bo nie da się zapisać nulla ale trzeba dodać response żeby nie pokazywało autora
    //bartek: zrobione
    //dziala
    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        adminRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        List<NotificationResponse> notifications = notificationService.getAllNotificationsDetails();
        return ResponseEntity.ok(notifications);
    }

    // recipients=jvjhvbm musi cos wyrzucac
    // Maciej: wyrzuca już
    //tutaj tez mozna zastosowac NotificationResponse tak jak wyzej
    //tak zrobie i zakomentuje to sie zapoznasz
    //dziala
    @GetMapping("/notifications/filter")
    public ResponseEntity<List<Notification>> getNotificationsByRecipients(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Recipients recipients) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        adminRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        List<Notification> notifications = notificationService.getNotificationsByRecipients(recipients);
        return ResponseEntity.ok(notifications);
    }

    //druga wersja jak chcesz
    /*@GetMapping("/notifications/filter")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByRecipients2(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Recipients recipients) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        adminRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        List<NotificationResponse> notifications = notificationService.getNotificationsByRecipientsWithoutDetails(recipients);
        return ResponseEntity.ok(notifications);
    }*/

    ////////////////////////////////////////////////////////////////////

    //dziala
    @GetMapping("/comments/reports/comment/{commentId}")
    public ResponseEntity<List<ReportedComment>> getAllReportsByComment(@PathVariable int commentId) {
        Comment comment = commentService.getCommentById(commentId);
        List<ReportedComment> reports = reportedCommentService.getAllReportsByComment(comment.getId());
        return ResponseEntity.ok(reports);
    }

    //dziala
    @GetMapping("/meals/reports/meal/{mealId}")
    public ResponseEntity<List<ReportedMeal>> getAllReportsByMeal(@PathVariable int mealId) {
        List<ReportedMeal> reports = reportedMealService.getAllReportsByMeal(mealId);
        return ResponseEntity.ok(reports);
    }

    //dziala
    @GetMapping("/comments/reports/all/{reportingId}")
    public ResponseEntity<List<ReportedComment>> getAllReportedCommentsByUser(@PathVariable int reportingId)
    {
        List<ReportedComment> reportedComments = reportedCommentService.getAllReportsByUser(reportingId);
        return ResponseEntity.ok(reportedComments);
    }

    //dziala
    @GetMapping("/meals/reports/all/{reportingId}")
    public ResponseEntity<List<ReportedMeal>> getAllReportedMealsByUser(@PathVariable int reportingId)
    {
        List<ReportedMeal> reportedComments = reportedMealService.getAllReportsByUser(reportingId);
        return ResponseEntity.ok(reportedComments);
    }
}
