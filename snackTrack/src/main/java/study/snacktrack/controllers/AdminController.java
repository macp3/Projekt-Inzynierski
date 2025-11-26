package study.snacktrack.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import study.snacktrack.dto.*;
import study.snacktrack.entities.*;
import study.snacktrack.entities.enums.Recipients;
import study.snacktrack.entities.enums.Status;

import study.snacktrack.repositories.AdminRepository;
import study.snacktrack.repositories.ExerciseRepository;
import study.snacktrack.repositories.TrainingInfoRepository;
import study.snacktrack.repositories.TrainingRepository;
import study.snacktrack.repositories.UserRepository;
import study.snacktrack.services.CommentService;
import study.snacktrack.services.FoodService;
import study.snacktrack.services.JwtService;
import study.snacktrack.services.MealService;
import study.snacktrack.services.NotificationService;
import study.snacktrack.services.PushNotificationService;
import study.snacktrack.services.ReportedCommentService;
import study.snacktrack.services.ReportedMealService;
import study.snacktrack.services.TrainingService;
import study.snacktrack.services.UserService;

/**
 * Controller handling admin-related functionality (users, reports, trainings,
 * exercises, notifications).
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    // --- FIELDS ---
    private final AdminRepository adminRepository;
    private final UserService userService;
    private final ReportedMealService reportedMealService;
    private final ReportedCommentService reportedCommentService;
    private final CommentService commentService;
    private final JwtService jwtService;
    private final TrainingService trainingService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final ExerciseRepository exerciseRepository;
    private final TrainingInfoRepository trainingInfoRepository;
    private final MealService mealService; // Potrzebne do usuwania posiłków przez admina

    // --- CONSTRUCTOR ---
    public AdminController(
            AdminRepository adminRepository,
            UserService userService,
            MealService mealService,
            CommentService commentService,
            ReportedMealService reportedMealService,
            ReportedCommentService reportedCommentService,
            FoodService foodService,
            JwtService jwtService,
            TrainingService trainingService,
            NotificationService notificationService,
            UserRepository userRepository,
            TrainingRepository trainingRepository,
            ExerciseRepository exerciseRepository,
            TrainingInfoRepository trainingInfoRepository) {
        this.adminRepository = adminRepository;
        this.userService = userService;
        this.mealService = mealService;
        this.commentService = commentService;
        this.reportedMealService = reportedMealService;
        this.reportedCommentService = reportedCommentService;
        this.jwtService = jwtService;
        this.trainingService = trainingService;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.trainingRepository = trainingRepository;
        this.exerciseRepository = exerciseRepository;
        this.trainingInfoRepository = trainingInfoRepository;
    }

    // ========================================================================
    // DASHBOARD & STATS
    // ========================================================================

    @GetMapping("/dashboard")
    public String getDashboard() {
        return "Witaj, ADMIN!";
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByStatus(Status.active);
        long bannedUsers = userRepository.countByStatus(Status.banned);
        long premiumUsers = userRepository.countByPremiumExpirationAfter(LocalDate.now());

        long totalTrainings = trainingInfoRepository.count();
        long totalExercises = exerciseRepository.count();

        return ResponseEntity.ok(new DashboardStatsResponse(
                totalUsers,
                activeUsers,
                bannedUsers,
                premiumUsers,
                totalTrainings,
                totalExercises));
    }

    // ========================================================================
    // USERS
    // ========================================================================

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable int userId) {
        try {
            return ResponseEntity.ok(userService.getUserById(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/user/{userId}/info/expirationDate")
    public ResponseEntity<?> updateExpirationDate(@PathVariable int userId, @RequestParam String dateString) {
        try {
            return ResponseEntity.ok(userService.updatePremiumExpiration(userId, dateString));
        } catch (IllegalArgumentException | HttpMessageNotReadableException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/user/{userId}/toggle-ban")
    public ResponseEntity<?> toggleUserBan(@PathVariable int userId) {
        try {
            userService.toggleUserBan(userId);
            return ResponseEntity.ok("User status toggled successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Returns PAGINATED list of users.
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(required = false) String query) {
        try {
            var userPage = userService.getUsersPage(page, size, query);
            return ResponseEntity.ok(userPage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ========================================================================
    // REPORTS & MODERATION (MEALS & COMMENTS)
    // ========================================================================

    // --- MEAL REPORTS ---

    @GetMapping("/reports/meals")
    public ResponseEntity<?> getAllMealReports() {
        try {
            // Zakładamy, że dodałeś metodę getAllReports() w ReportedMealService
            return ResponseEntity.ok(reportedMealService.getAllReports());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/reports/meals/{reportId}")
    public ResponseEntity<?> resolveMealReport(@PathVariable int reportId) {
        try {
            reportedMealService.deleteReport(reportId);
            return ResponseEntity.ok("Report resolved.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- COMMENT REPORTS ---

    @GetMapping("/reports/comments")
    public ResponseEntity<?> getAllCommentReports() {
        try {
            return ResponseEntity.ok(reportedCommentService.getAllReports());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/reports/comments/{reportId}")
    public ResponseEntity<?> resolveCommentReport(@PathVariable int reportId) {
        try {
            reportedCommentService.deleteReport(reportId);
            return ResponseEntity.ok("Report resolved.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- CONTENT DELETION (ADMIN GOD MODE) ---

    @DeleteMapping("/meals/{mealId}/delete")
    public ResponseEntity<?> deleteMealAsAdmin(@PathVariable int mealId) {
        try {
            mealService.deleteMealAsAdmin(mealId); // Wymaga dodania metody w MealService
            return ResponseEntity.ok("Meal deleted by admin.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Opcjonalnie dla komentarzy:
    // @DeleteMapping("/comments/{commentId}/delete") ...

    // ========================================================================
    // TRAININGS & EXERCISES
    // ========================================================================

    @GetMapping("/trainings")
    public ResponseEntity<?> getAllTrainings() {
        try {
            return ResponseEntity.ok(trainingService.getAllTrainings());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/trainings/{trainingId}/details")
    public ResponseEntity<?> getTrainingDetails(@PathVariable int trainingId) {
        try {
            return ResponseEntity.ok(trainingService.getTrainingDetails(trainingId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/trainings/add")
    public ResponseEntity<String> addTraining(@RequestBody TrainingRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            Admin admin = adminRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

            trainingService.createTraining(request, admin.getId());
            return ResponseEntity.ok("Training added successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/trainings/{trainingId}/edit")
    public ResponseEntity<String> editTraining(@PathVariable int trainingId, @RequestBody TrainingRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            Admin admin = adminRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

            trainingService.editTraining(request, admin.getId(), trainingId);
            return ResponseEntity.ok("Training edited successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/trainings/addExercise")
    public ResponseEntity<?> addExerciseToTraining(@RequestBody AddExerciseToTrainingRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            Admin admin = adminRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

            var response = trainingService.addExerciseToTraining(
                    request.getExerciseId(),
                    request.getTrainingId(),
                    admin.getId(),
                    request.getDayOfExercise());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/trainings/{trainingId}/delete/{exerciseId}")
    public ResponseEntity<?> deleteExercisesByIdFromTraining(@PathVariable int trainingId,
            @PathVariable int exerciseId) {
        try {
            var response = trainingService.deleteAllExercisesByIdFromTraining(trainingId, exerciseId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/trainings/{trainingId}/delete/{exerciseId}/{dayOfExercise}")
    public ResponseEntity<?> deleteExerciseByIdAndDayFromTraining(@PathVariable int trainingId,
            @PathVariable int exerciseId, @PathVariable int dayOfExercise) {
        try {
            var response = trainingService.deleteExerciseByIdAndDayFromTraining(trainingId, exerciseId, dayOfExercise);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/trainings/{trainingId}/delete")
    public ResponseEntity<String> deleteTraining(@PathVariable int trainingId) {
        try {
            trainingService.deleteTraining(trainingId);
            return ResponseEntity.ok("Training deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/exercises")
    public ResponseEntity<?> getAllExercises() {
        try {
            return ResponseEntity.ok(trainingService.getAllExercises());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/exercises/{exerciseId}/details")
    public ResponseEntity<?> getExerciseDetails(@PathVariable int exerciseId) {
        try {
            return ResponseEntity.ok(trainingService.getExerciseById(exerciseId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/exercises/add")
    public ResponseEntity<?> addExercise(@RequestBody ExerciseRequest request) {
        try {
            var exercise = trainingService.createExercise(
                    request.getName(),
                    request.getDescription(),
                    request.getType(),
                    request.getDifficulty(),
                    request.getNumberOfSets(),
                    request.getRepetitionsPerSet());
            return ResponseEntity.ok(exercise);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/exercises/delete/{exerciseId}")
    public ResponseEntity<String> deleteExercise(@PathVariable int exerciseId) {
        try {
            trainingService.deleteExercise(exerciseId);
            return ResponseEntity.ok("Exercise deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ========================================================================
    // NOTIFICATIONS
    // ========================================================================

    @Autowired
    private PushNotificationService pushNotificationService;

    @PostMapping("/notifications/add")
    public ResponseEntity<String> createNotification(@RequestBody NotificationRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            Admin admin = adminRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

            notificationService.createNotification(request, admin);
            return ResponseEntity.ok("Notification created successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/notifications")
    public ResponseEntity<?> getAllNotifications(@RequestHeader("Authorization") String authHeader) {
        try {
            // Sprawdzenie tokena (można to też załatwić przez SecurityConfig)
            String token = authHeader.replace("Bearer ", "");
            jwtService.extractEmail(token);

            return ResponseEntity.ok(notificationService.getAllNotificationsDetails());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/notifications/filter")
    public ResponseEntity<?> getNotificationsByRecipients(@RequestHeader("Authorization") String authHeader,
            @RequestParam Recipients recipients) {
        try {
            String token = authHeader.replace("Bearer ", "");
            jwtService.extractEmail(token);

            return ResponseEntity.ok(notificationService.getNotificationsByRecipientsWithoutDetails(recipients));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/sendNotification")
    public ResponseEntity<String> sendNotification(@RequestBody Map<String, String> payload) {
        try {
            String group = payload.get("group");
            String title = payload.get("title");
            String body = payload.get("body");

            pushNotificationService.sendToGroup(group, title, body);
            return ResponseEntity.ok("Notification sent to group: " + group);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/meals/{mealId}")
    public ResponseEntity<?> getMealDetailsForAdmin(@PathVariable int mealId) {
        try {
            // Używamy existing service method (zakładam, że zwraca Meal lub MealResponse)
            return ResponseEntity.ok(mealService.getMealWithIngredients(mealId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Content not found or deleted: " + e.getMessage());
        }
    }

    @GetMapping("/comments/{commentId}")
    public ResponseEntity<?> getCommentDetailsForAdmin(@PathVariable int commentId) {
        try {
            // Musisz mieć metodę getCommentById w CommentService
            return ResponseEntity.ok(commentService.getCommentById(commentId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Content not found or deleted: " + e.getMessage());
        }
    }

    @GetMapping("/meals")
    public ResponseEntity<?> getAllMeals() {
        try {
            return ResponseEntity.ok(mealService.getAllMeals());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/comments/{commentId}/delete")
    public ResponseEntity<?> deleteCommentAsAdmin(@PathVariable int commentId) {
        try {
            commentService.deleteCommentAsAdmin(commentId);
            return ResponseEntity.ok("Comment deleted by admin.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}