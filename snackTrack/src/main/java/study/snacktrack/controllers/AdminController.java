package study.snacktrack.controllers;

import java.time.LocalDate;
import java.util.List;

import study.snacktrack.entities.enums.Recipients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import study.snacktrack.dto.AddExerciseToTrainingRequest;
import study.snacktrack.dto.ExerciseRequest;
import study.snacktrack.dto.NotificationRequest;
import study.snacktrack.dto.TrainingDetailsResponse;
import study.snacktrack.dto.TrainingRequest;
import study.snacktrack.entities.Admin;
import study.snacktrack.entities.Exercise;
import study.snacktrack.entities.Notification;
import study.snacktrack.entities.ReportedComment;
import study.snacktrack.entities.ReportedMeal;
import study.snacktrack.entities.TrainingInfo;
import study.snacktrack.entities.User;
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

    // admin
    // pozniej przetestujemy bo trzeba zrestartowac
    @PutMapping("/user/{userId}/info/expirationDate")
    public ResponseEntity<User> updateExpirationDate(@PathVariable int userId, @RequestParam LocalDate date) {
        User user = userService.getUserById(userId);
        userService.updatePremiumExpiration(user.getId(), date);
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
    // przetestujemy pozniej bo odjebalem
    @GetMapping("/reports/meals/user/{userId}")
    public ResponseEntity<List<ReportedMeal>> getAllMealReportsByUser(@PathVariable int userId) {
        List<ReportedMeal> reports = reportedMealService.getAllReportsByUser(userId);
        return ResponseEntity.ok(reports);
    }

    // admin
    // pozniej bo trzeba zrobic report i tu tez odpierdolilem
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

    //
    @GetMapping("/trainings/{trainingId}/details")
    public ResponseEntity<TrainingDetailsResponse> getTrainingDetails(@PathVariable int trainingId) {
        return ResponseEntity.ok(trainingService.getTrainingDetails(trainingId));
    }

    // dodac walidacje duration time
    // walidacja exeercise id musi byc z bazy

    @PostMapping("/trainings/add")
    public ResponseEntity<String> addTraining(@RequestBody TrainingRequest request) {
        trainingService.createTraining(request, 1);
        return ResponseEntity.ok("Training added successfully");
    }

    // dodac walidacje exercise id musi byc w bazie
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

    // dodawanie cwiczenia do treningu
    // nie wykrywa trainingid a w bazie on istnieje
    // potem przetestuj wszystko
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

    // nie znajduje training o id 1 a jest on w bazie
    // potem przetestuj wszystko
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

    // wszystkie pola z intami maja byc przyrownane do null i bedzie dzialac
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

    // wyjebac login, email, password xd
    // walidacja stringow - nic nie wyrzuca exeption
    // porownanie do nulli tak samo
    // Maciej: zrobione
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
    // MAciej: już nie będą bo nie da się zapisać nulla
    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getAllNotifications(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        adminRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    // recipients=gowno musi cos wyrzucac
    // Maciej: wyrzuca już
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
}
