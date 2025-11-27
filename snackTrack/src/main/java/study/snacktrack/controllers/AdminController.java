package study.snacktrack.controllers;

import java.time.LocalDate;
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
import study.snacktrack.repositories.UserRepository;
import study.snacktrack.services.CommentService;
import study.snacktrack.services.JwtService;
import study.snacktrack.services.MealService;
import study.snacktrack.services.NotificationService;
import study.snacktrack.services.PushNotificationService;
import study.snacktrack.services.ReportedCommentService;
import study.snacktrack.services.ReportedMealService;
import study.snacktrack.services.TrainingService;
import study.snacktrack.services.UserService;

/**
 * REST Controller providing administrative endpoints.
 * Handles user management, content moderation (reports, meals, comments),
 * and managing trainings, exercises, and notifications.
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    // --- FIELDS ---
    /** Repository for Admin entity access. */
    private final AdminRepository adminRepository;
    /** Service for user related business logic. */
    private final UserService userService;
    /** Service for reported meal management. */
    private final ReportedMealService reportedMealService;
    /** Service for reported comment management. */
    private final ReportedCommentService reportedCommentService;
    /** Service for comment related operations. */
    private final CommentService commentService;
    /** Service for JWT token handling. */
    private final JwtService jwtService;
    /** Service for training and exercise business logic. */
    private final TrainingService trainingService;
    /** Service for application notifications. */
    private final NotificationService notificationService;
    /** Repository for User entity access. */
    private final UserRepository userRepository;

    /** Repository for Exercise entity access. */
    private final ExerciseRepository exerciseRepository;
    /** Repository for TrainingInfo entity access. */
    private final TrainingInfoRepository trainingInfoRepository;
    /** Service for meal related operations, used here for admin deletion. */
    private final MealService mealService;

    // --- CONSTRUCTOR ---
    /**
     * Constructs the AdminController with all necessary dependencies.
     */
    public AdminController(
            AdminRepository adminRepository,
            UserService userService,
            MealService mealService,
            CommentService commentService,
            ReportedMealService reportedMealService,
            ReportedCommentService reportedCommentService,
            JwtService jwtService,
            TrainingService trainingService,
            NotificationService notificationService,
            UserRepository userRepository,
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
        this.exerciseRepository = exerciseRepository;
        this.trainingInfoRepository = trainingInfoRepository;
    }

    // ========================================================================
    // DASHBOARD & STATS
    // ========================================================================

    /**
     * Retrieves key statistics for the administration dashboard.
     * Includes counts for users (total, active, banned, premium), trainings, and
     * exercises.
     *
     * @return ResponseEntity containing the dashboard statistics DTO.
     */
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

    /**
     * Retrieves detailed information for a specific user.
     *
     * @param userId The ID of the user to retrieve.
     * @return ResponseEntity containing user details or an error message.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable int userId) {
        try {
            return ResponseEntity.ok(userService.getUserById(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Updates the premium subscription expiration date for a user.
     *
     * @param userId     The ID of the user to update.
     * @param dateString The new expiration date as a string.
     * @return ResponseEntity with the updated user info or an error message.
     */
    @PutMapping("/user/{userId}/info/expirationDate")
    public ResponseEntity<?> updateExpirationDate(@PathVariable int userId, @RequestParam String dateString) {
        try {
            return ResponseEntity.ok(userService.updatePremiumExpiration(userId, dateString));
        } catch (IllegalArgumentException | HttpMessageNotReadableException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Toggles the ban status (active/banned) for a user.
     *
     * @param userId The ID of the user to ban/unban.
     * @return ResponseEntity with success or an error message.
     */
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
     * Returns a paginated list of all users, optionally filtered by a query string.
     *
     * @param page  The requested page number (default 0).
     * @param size  The number of records per page (default 25).
     * @param query Optional search query for filtering users.
     * @return ResponseEntity containing the paginated user list.
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

    /**
     * Retrieves all reported meals for moderation.
     *
     * @return ResponseEntity containing a list of reported meals.
     */
    @GetMapping("/reports/meals")
    public ResponseEntity<?> getAllMealReports() {
        try {
            return ResponseEntity.ok(reportedMealService.getAllReports());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Resolves and deletes a specific meal report.
     *
     * @param reportId The ID of the meal report to resolve.
     * @return ResponseEntity with success or an error message.
     */
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

    /**
     * Retrieves all reported comments for moderation.
     *
     * @return ResponseEntity containing a list of reported comments.
     */
    @GetMapping("/reports/comments")
    public ResponseEntity<?> getAllCommentReports() {
        try {
            return ResponseEntity.ok(reportedCommentService.getAllReports());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Resolves and deletes a specific comment report.
     *
     * @param reportId The ID of the comment report to resolve.
     * @return ResponseEntity with success or an error message.
     */
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

    /**
     * Permanently deletes a meal by its ID (Admin functionality).
     *
     * @param mealId The ID of the meal to delete.
     * @return ResponseEntity with success or an error message.
     */
    @DeleteMapping("/meals/{mealId}/delete")
    public ResponseEntity<?> deleteMealAsAdmin(@PathVariable int mealId) {
        try {
            mealService.deleteMealAsAdmin(mealId);
            return ResponseEntity.ok("Meal deleted by admin.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ========================================================================
    // TRAININGS & EXERCISES
    // ========================================================================

    /**
     * Retrieves all available training programs.
     *
     * @return ResponseEntity containing a list of all trainings.
     */
    @GetMapping("/trainings")
    public ResponseEntity<?> getAllTrainings() {
        try {
            return ResponseEntity.ok(trainingService.getAllTrainings());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieves detailed information for a specific training program.
     *
     * @param trainingId The ID of the training to retrieve details for.
     * @return ResponseEntity containing training details or an error message.
     */
    @GetMapping("/trainings/{trainingId}/details")
    public ResponseEntity<?> getTrainingDetails(@PathVariable int trainingId) {
        try {
            return ResponseEntity.ok(trainingService.getTrainingDetails(trainingId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Creates a new training program.
     * Requires Admin authentication via Authorization header.
     *
     * @param request    The TrainingRequest DTO containing training data.
     * @param authHeader The Authorization header containing the JWT token.
     * @return ResponseEntity with success message or an error message.
     */
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

    /**
     * Edits an existing training program.
     * Requires Admin authentication via Authorization header.
     *
     * @param trainingId The ID of the training to edit.
     * @param request    The TrainingRequest DTO containing updated data.
     * @param authHeader The Authorization header containing the JWT token.
     * @return ResponseEntity with success message or an error message.
     */
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

    /**
     * Adds an exercise to a specific training program on a specific day.
     * Requires Admin authentication via Authorization header.
     *
     * @param request    The AddExerciseToTrainingRequest DTO.
     * @param authHeader The Authorization header containing the JWT token.
     * @return ResponseEntity with the updated training structure or an error
     *         message.
     */
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

    /**
     * Deletes all occurrences of a specific exercise ID from a training program.
     *
     * @param trainingId The ID of the training program.
     * @param exerciseId The ID of the exercise to delete.
     * @return ResponseEntity with the updated training structure or an error
     *         message.
     */
    @DeleteMapping("/trainings/{trainingId}/delete/{exerciseId}")
    public ResponseEntity<?> deleteAllExercisesByIdFromTraining(@PathVariable int trainingId,
            @PathVariable int exerciseId) {
        try {
            var response = trainingService.deleteAllExercisesByIdFromTraining(trainingId, exerciseId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Deletes a specific instance of an exercise defined by its ID and day from a
     * training program.
     *
     * @param trainingId    The ID of the training program.
     * @param exerciseId    The ID of the exercise to delete.
     * @param dayOfExercise The day the exercise occurs on.
     * @return ResponseEntity with the updated training structure or an error
     *         message.
     */
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

    /**
     * Permanently deletes a training program by its ID.
     *
     * @param trainingId The ID of the training to delete.
     * @return ResponseEntity with success or an error message.
     */
    @DeleteMapping("/trainings/{trainingId}/delete")
    public ResponseEntity<String> deleteTraining(@PathVariable int trainingId) {
        try {
            trainingService.deleteTraining(trainingId);
            return ResponseEntity.ok("Training deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieves all available exercises.
     *
     * @return ResponseEntity containing a list of all exercises.
     */
    @GetMapping("/exercises")
    public ResponseEntity<?> getAllExercises() {
        try {
            return ResponseEntity.ok(trainingService.getAllExercises());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieves detailed information for a specific exercise.
     *
     * @param exerciseId The ID of the exercise to retrieve details for.
     * @return ResponseEntity containing exercise details or an error message.
     */
    @GetMapping("/exercises/{exerciseId}/details")
    public ResponseEntity<?> getExerciseDetails(@PathVariable int exerciseId) {
        try {
            return ResponseEntity.ok(trainingService.getExerciseById(exerciseId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Creates a new exercise.
     *
     * @param request The ExerciseRequest DTO containing exercise parameters.
     * @return ResponseEntity with the created exercise object or an error message.
     */
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

    /**
     * Permanently deletes an exercise by its ID.
     *
     * @param exerciseId The ID of the exercise to delete.
     * @return ResponseEntity with success or an error message.
     */
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

    /**
     * Service for handling push notifications (FCM).
     */
    @Autowired
    private PushNotificationService pushNotificationService;

    /**
     * Creates a new persistent notification in the database.
     * Requires Admin authentication via Authorization header.
     *
     * @param request    The NotificationRequest DTO.
     * @param authHeader The Authorization header containing the JWT token.
     * @return ResponseEntity with success message or an error message.
     */
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

    /**
     * Retrieves all saved notifications with details.
     * Requires Admin authentication via Authorization header.
     *
     * @param authHeader The Authorization header containing the JWT token.
     * @return ResponseEntity containing a list of notification details.
     */
    @GetMapping("/notifications")
    public ResponseEntity<?> getAllNotifications(@RequestHeader("Authorization") String authHeader) {
        try {
            // Token check (can also be handled via SecurityConfig)
            String token = authHeader.replace("Bearer ", "");
            jwtService.extractEmail(token);

            return ResponseEntity.ok(notificationService.getAllNotificationsDetails());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieves notifications filtered by recipient group.
     * Requires Admin authentication via Authorization header.
     *
     * @param authHeader The Authorization header containing the JWT token.
     * @param recipients The recipient group (e.g., ALL, PREMIUM, BANNED).
     * @return ResponseEntity containing a list of notifications.
     */
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

    /**
     * Sends a push notification immediately to a specified user group via FCM.
     *
     * @param payload Map containing 'group', 'title', and 'body' for the
     *                notification.
     * @return ResponseEntity with success message or an error message.
     */
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

    /**
     * Retrieves detailed information about a specific meal for administrative
     * review.
     *
     * @param mealId The ID of the meal.
     * @return ResponseEntity containing meal details or an error message.
     */
    @GetMapping("/meals/{mealId}")
    public ResponseEntity<?> getMealDetailsForAdmin(@PathVariable int mealId) {
        try {
            return ResponseEntity.ok(mealService.getMealWithIngredients(mealId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Content not found or deleted: " + e.getMessage());
        }
    }

    /**
     * Retrieves detailed information about a specific comment for administrative
     * review.
     *
     * @param commentId The ID of the comment.
     * @return ResponseEntity containing comment details or an error message.
     */
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<?> getCommentDetailsForAdmin(@PathVariable int commentId) {
        try {
            return ResponseEntity.ok(commentService.getCommentById(commentId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Content not found or deleted: " + e.getMessage());
        }
    }

    /**
     * Retrieves a list of all meals (potentially simplified view).
     *
     * @return ResponseEntity containing a list of all meals.
     */
    @GetMapping("/meals")
    public ResponseEntity<?> getAllMeals() {
        try {
            return ResponseEntity.ok(mealService.getAllMeals());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Permanently deletes a comment by its ID (Admin functionality).
     *
     * @param commentId The ID of the comment to delete.
     * @return ResponseEntity with success or an error message.
     */
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