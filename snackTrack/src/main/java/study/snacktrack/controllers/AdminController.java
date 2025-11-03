package study.snacktrack.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
import study.snacktrack.services.PushNotificationService;
import study.snacktrack.services.ReportedCommentService;
import study.snacktrack.services.ReportedMealService;
import study.snacktrack.services.TrainingService;
import study.snacktrack.services.UserService;

/**
 * Controller handling admin-related functionality (users, reports, trainings,
 * exercises, notifications).
 * Provides administrative operations such as adding trainings, viewing reports,
 * sending notifications, etc.
 */
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

    /**
     * Constructs a new AdminController.
     *
     * @param adminRepository        repository for admin data
     * @param userService            user management service
     * @param mealService            meal service (unused directly here but required
     *                               for injection)
     * @param commentService         comment management service
     * @param reportedMealService    service managing reported meals
     * @param reportedCommentService service managing reported comments
     * @param foodService            food service (unused directly here but required
     *                               for injection)
     * @param jwtService             JWT token validation service
     * @param trainingService        training management service
     * @param notificationService    notification management service
     */
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

    /**
     * Test endpoint to verify that admin panel is operational.
     *
     * @return welcome message for admin
     */
    @GetMapping("/dashboard")
    public String getDashboard() {
        return "Witaj, ADMIN!";
    }

    /**
     * Retrieves user details by user ID.
     *
     * @param userId ID of the user
     * @return User object or error message
     * @throws IllegalArgumentException if user cannot be found
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable int userId) {
        User user;
        try {
            user = userService.getUserById(userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(user);
    }

    /**
     * Updates premium expiration date for selected user.
     *
     * @param userId     user whose premium expiration date will be updated
     * @param dateString date in string format (yyyy-MM-dd expected)
     * @return updated User object or error message
     * @throws IllegalArgumentException when invalid user ID or date format
     */
    @PutMapping("/user/{userId}/info/expirationDate")
    public ResponseEntity<?> updateExpirationDate(@PathVariable int userId, @RequestParam String dateString) {
        User user;
        try {
            user = userService.updatePremiumExpiration(userId, dateString);
        } catch (IllegalArgumentException | HttpMessageNotReadableException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(user);
    }

    /**
     * Returns list of all users in the database.
     *
     * @return list of users or error message
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> allUsers;
        try {
            allUsers = userService.getAllUsers();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(allUsers);
    }

    /**
     * Retrieves all meal reports created by a specific user.
     *
     * @param userId ID of the reporting user
     * @return list of reported meals or error message
     */
    @GetMapping("/reports/meals/user/{userId}")
    public ResponseEntity<?> getAllMealReportsByUser(@PathVariable int userId) {
        List<ReportedMeal> reports;
        try {
            reports = reportedMealService.getAllReportsByUser(userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(reports);
    }

    /**
     * Retrieves all comment reports created by a specific user.
     *
     * @param userId ID of user who reported comments
     * @return list of reported comments or error message
     */
    @GetMapping("/reports/comments/user/{userId}")
    public ResponseEntity<?> getAllCommentReportsByUser(@PathVariable int userId) {
        List<ReportedComment> reports;
        try {
            reports = reportedCommentService.getAllReportsByUser(userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(reports);
    }

    // ========================================================================
    // TRAININGS
    // ========================================================================

    /**
     * Returns all Trainings without exercises and details.
     *
     * @return list of training summaries
     */
    @GetMapping("/trainings")
    public ResponseEntity<?> getAllTrainings() {
        List<TrainingInfo> allTrainings;
        try {
            allTrainings = trainingService.getAllTrainings();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(allTrainings);
    }

    /**
     * Returns detailed information about training including exercises and assigned
     * days.
     *
     * @param trainingId ID of the training
     * @return training details or error message
     */
    @GetMapping("/trainings/{trainingId}/details")
    public ResponseEntity<?> getTrainingDetails(@PathVariable int trainingId) {
        TrainingDetailsResponse response;
        try {
            response = trainingService.getTrainingDetails(trainingId);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Creates and assigns a new training plan.
     *
     * @param request    training creation payload
     * @param authHeader Authorization token (Bearer)
     * @return success message or error
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
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NullPointerException e) {
            return ResponseEntity.badRequest().body("Something went wrong: " + e.getMessage());
        }
        return ResponseEntity.ok("Training added successfully");
    }

    /**
     * Edits an existing training plan.
     *
     * @param trainingId ID of training to edit
     * @param request    updated training data
     * @param authHeader Authorization token
     * @return success or error message
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
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Training edited successfully");
    }

    /**
     * Adds an exercise to an existing training.
     *
     * @param request    request containing exerciseId, trainingId, and day of
     *                   exercise
     * @param authHeader Authorization token
     * @return modified training details or error
     */
    @PostMapping("/trainings/addExercise")
    public ResponseEntity<?> addExerciseToTraining(@RequestBody AddExerciseToTrainingRequest request,
            @RequestHeader("Authorization") String authHeader) {
        TrainingDetailsResponse response;
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            Admin admin = adminRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

            response = trainingService.addExerciseToTraining(
                    request.getExerciseId(),
                    request.getTrainingId(),
                    admin.getId(),
                    request.getDayOfExercise());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes ALL occurrences of a specific exercise from training.
     *
     * @param trainingId training identifier
     * @param exerciseId exercise identifier
     * @return updated training or error message
     */
    @DeleteMapping("/trainings/{trainingId}/delete/{exerciseId}")
    public ResponseEntity<?> deleteExercisesByIdFromTraining(@PathVariable int trainingId,
            @PathVariable int exerciseId) {
        TrainingDetailsResponse response;
        try {
            response = trainingService.deleteAllExercisesByIdFromTraining(trainingId, exerciseId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes an exercise from a training on a specific day.
     *
     * @param trainingId    training identifier
     * @param exerciseId    exercise identifier
     * @param dayOfExercise day in the workout plan
     * @return updated training details
     */
    @DeleteMapping("/trainings/{trainingId}/delete/{exerciseId}/{dayOfExercise}")
    public ResponseEntity<?> deleteExerciseByIdAndDayFromTraining(@PathVariable int trainingId,
            @PathVariable int exerciseId, @PathVariable int dayOfExercise) {
        TrainingDetailsResponse response;
        try {
            response = trainingService.deleteExerciseByIdAndDayFromTraining(trainingId, exerciseId, dayOfExercise);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes an entire training plan.
     *
     * @param trainingId ID of training to be deleted
     * @return success or error message
     */
    @DeleteMapping("/trainings/{trainingId}/delete")
    public ResponseEntity<String> deleteTraining(@PathVariable int trainingId) {
        try {
            trainingService.deleteTraining(trainingId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Training deleted successfully");
    }

    /**
     * Returns all exercises.
     *
     * @return list of exercises
     */
    @GetMapping("/exercises")
    public ResponseEntity<?> getAllExercises() {
        List<Exercise> exercises;
        try {
            exercises = trainingService.getAllExercises();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(exercises);
    }

    /**
     * Retrieves full exercise details.
     *
     * @param exerciseId ID of exercise to retrieve
     * @return exercise or error
     */
    @GetMapping("/exercises/{exerciseId}/details")
    public ResponseEntity<?> getExerciseDetails(@PathVariable int exerciseId) {
        Exercise exercise;
        try {
            exercise = trainingService.getExerciseById(exerciseId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(exercise);
    }

    /**
     * Creates a new exercise.
     *
     * @param request exercise payload
     * @return created exercise or error
     */
    @PostMapping("/exercises/add")
    public ResponseEntity<?> addExercise(@RequestBody ExerciseRequest request) {
        Exercise exercise;
        try {
            exercise = trainingService.createExercise(request.getName(), request.getDescription(),
                    request.getType(), request.getDifficulty(), request.getNumberOfSets(),
                    request.getRepetitionsPerSet());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(exercise);
    }

    /**
     * Deletes exercise by ID.
     *
     * @param exerciseId ID of exercise
     * @return success message or error
     */
    @DeleteMapping("/exercises/delete/{exerciseId}")
    public ResponseEntity<String> deleteExercise(@PathVariable int exerciseId) {
        try {
            trainingService.deleteExercise(exerciseId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Exercise deleted successfully");
    }

    // ========================================================================
    // NOTIFICATIONS
    // ========================================================================

    /**
     * Creates a notification and sends it to selected recipients.
     *
     * @param request    notification content and recipients
     * @param authHeader Authorization token
     * @return success message or error
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
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Notification created successfully");
    }

    /**
     * Returns all notifications without exposing admin author details.
     *
     * @param authHeader Authorization token
     * @return list of notifications
     */
    @GetMapping("/notifications")
    public ResponseEntity<?> getAllNotifications(@RequestHeader("Authorization") String authHeader) {
        List<NotificationResponse> notifications;
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            adminRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

            notifications = notificationService.getAllNotificationsDetails();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(notifications);
    }

    /**
     * Filters notifications by recipient category (premium, non_premium, all).
     *
     * @param authHeader Authorization token
     * @param recipients enum defining target notification recipients
     * @return filtered list of notifications
     */
    @GetMapping("/notifications/filter")
    public ResponseEntity<?> getNotificationsByRecipients2(@RequestHeader("Authorization") String authHeader,
            @RequestParam Recipients recipients) {
        List<NotificationResponse> notifications;
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            adminRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

            notifications = notificationService
                    .getNotificationsByRecipientsWithoutDetails(recipients);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(notifications);
    }

    // ========================================================================
    // REPORTS (COMMENTS & MEALS)
    // ========================================================================

    /**
     * Retrieves all reports related to a specific comment.
     *
     * @param commentId ID of comment
     * @return list of reports or error
     */
    @GetMapping("/comments/reports/comment/{commentId}")
    public ResponseEntity<?> getAllReportsByComment(@PathVariable int commentId) {
        List<ReportedComment> reports;
        try {
            Comment comment = commentService.getCommentById(commentId);
            reports = reportedCommentService.getAllReportsByComment(comment.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(reports);
    }

    /**
     * Retrieves all reports related to a specific meal.
     *
     * @param mealId ID of meal
     * @return list of reports or error
     */
    @GetMapping("/meals/reports/meal/{mealId}")
    public ResponseEntity<?> getAllReportsByMeal(@PathVariable int mealId) {
        List<ReportedMeal> reports;
        try {
            reports = reportedMealService.getAllReportsByMeal(mealId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(reports);
    }

    /**
     * Retrieves all comment reports submitted by a specific user.
     *
     * @param reportingId ID of reporting user
     * @return list of reported comments
     */
    @GetMapping("/comments/reports/all/{reportingId}")
    public ResponseEntity<?> getAllReportedCommentsByUser(@PathVariable int reportingId) {
        List<ReportedComment> reportedComments;
        try {
            reportedComments = reportedCommentService.getAllReportsByUser(reportingId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(reportedComments);
    }

    /**
     * Retrieves all meal reports submitted by a specific user.
     *
     * @param reportingId ID of reporting user
     * @return list of reported meals
     */
    @GetMapping("/meals/reports/all/{reportingId}")
    public ResponseEntity<?> getAllReportedMealsByUser(@PathVariable int reportingId) {
        List<ReportedMeal> reportedComments;
        try {
            reportedComments = reportedMealService.getAllReportsByUser(reportingId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(reportedComments);
    }

    @Autowired
    private PushNotificationService pushNotificationService;

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
}
