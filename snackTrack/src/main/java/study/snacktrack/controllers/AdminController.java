package study.snacktrack.controllers;

import java.time.LocalDate;
import java.util.List;

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

    //dziala
    @GetMapping("/dashboard")
    public String getDashboard() {
        return "Witaj, ADMIN!";
    }

    // nowe
    //dziala
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable int userId)
    {
        User user;
        try
        {
            user = userService.getUserById(userId);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(user);
    }

    //pozniej
    //dziala
    @PutMapping("/user/{userId}/info/expirationDate")
    public ResponseEntity<?> updateExpirationDate(@PathVariable int userId, @RequestParam String dateString)
    {
        User user;
        try
        {
            user = userService.updatePremiumExpiration(userId, dateString);
        }
        catch(IllegalArgumentException | HttpMessageNotReadableException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(user);
    }

    // admin
    //dziala
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers()
    {
        List<User> allUsers;
        try
        {
            allUsers = userService.getAllUsers();
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(allUsers);
    }

    // admin
    //dziala
    @GetMapping("/reports/meals/user/{userId}")
    public ResponseEntity<?> getAllMealReportsByUser(@PathVariable int userId)
    {
        List<ReportedMeal> reports;
        try
        {
            reports = reportedMealService.getAllReportsByUser(userId);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(reports);
    }

    // admin
    //dziala
    @GetMapping("/reports/comments/user/{userId}")
    public ResponseEntity<?> getAllCommentReportsByUser(@PathVariable int userId)
    {
        List<ReportedComment> reports;
        try
        {
            reports = reportedCommentService.getAllReportsByUser(userId);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(reports);
    }

    ////////////////////////////////////////////////////////////////////////

    //dziala
    @GetMapping("/trainings")
    public ResponseEntity<?> getAllTrainings()
    {
        List<TrainingInfo> allTrainings;
        try
        {
            allTrainings = trainingService.getAllTrainings();
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(allTrainings);
    }

    //dziala
    @GetMapping("/trainings/{trainingId}/details")
    public ResponseEntity<?> getTrainingDetails(@PathVariable int trainingId)
    {
        TrainingDetailsResponse response;
        try
        {
            response = trainingService.getTrainingDetails(trainingId);
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    //pozniej - duration time do nulla porownaj
    //dziala
    @PostMapping("/trainings/add")
    public ResponseEntity<String> addTraining(@RequestBody TrainingRequest request, @RequestHeader("Authorization") String authHeader)
    {
        try
        {
            // walidacja admina
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            Admin admin = adminRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
            trainingService.createTraining(request, admin.getId());
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch(NullPointerException e)
        {
            return ResponseEntity.badRequest().body("Something went wrong: " + e.getMessage());
        }
        return ResponseEntity.ok("Training added successfully");
    }

    //dziala
    @PutMapping("/trainings/{trainingId}/edit")
    public ResponseEntity<String> editTraining(@PathVariable int trainingId, @RequestBody TrainingRequest request, @RequestHeader("Authorization") String authHeader)
    {
        try
        {
            // walidacja admina
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            Admin admin = adminRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

            trainingService.editTraining(request, admin.getId(), trainingId);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Training edited successfully");
    }

    //dziala
    @PostMapping("/trainings/addExercise")
    public ResponseEntity<?> addExerciseToTraining(@RequestBody AddExerciseToTrainingRequest request, @RequestHeader("Authorization") String authHeader)
    {
        TrainingDetailsResponse response;
        try
        {
            // walidacja admina
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            Admin admin = adminRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

            response = trainingService.addExerciseToTraining(
                    request.getExerciseId(),
                    request.getTrainingId(),
                    admin.getId(),
                    request.getDayOfExercise());
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    //dziala
    @DeleteMapping("/trainings/{trainingId}/delete/{exerciseId}")
    public ResponseEntity<?> deleteExercisesByIdFromTraining(@PathVariable int trainingId, @PathVariable int exerciseId)
    {
        TrainingDetailsResponse response;
        try
        {
            response = trainingService.deleteAllExercisesByIdFromTraining(trainingId, exerciseId);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    //dziala
    @DeleteMapping("/trainings/{trainingId}/delete/{exerciseId}/{dayOfExercise}")
    public ResponseEntity<?> deleteExerciseByIdAndDayFromTraining(@PathVariable int trainingId, @PathVariable int exerciseId, @PathVariable int dayOfExercise)
    {
        TrainingDetailsResponse response;
        try
        {
            response = trainingService.deleteExerciseByIdAndDayFromTraining(trainingId, exerciseId, dayOfExercise);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    //dziala
    @DeleteMapping("/trainings/{trainingId}/delete")
    public ResponseEntity<String> deleteTraining(@PathVariable int trainingId)
    {
        try
        {
            trainingService.deleteTraining(trainingId);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Training deleted successfully");
    }

    //dziala
    @GetMapping("/exercises")
    public ResponseEntity<?> getAllExercises()
    {
        List<Exercise> exercises;
        try
        {
            exercises = trainingService.getAllExercises();
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(exercises);
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
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(exercise);
    }

    //dziala
    @PostMapping("/exercises/add")
    public ResponseEntity<?> addExercise(@RequestBody ExerciseRequest request)
    {
        Exercise exercise;
        try
        {
            exercise = trainingService.createExercise(request.getName(), request.getDescription(),
                    request.getType(), request.getDifficulty(), request.getNumberOfSets(), request.getRepetitionsPerSet());
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(exercise);
    }

    //dziala
    @DeleteMapping("/exercises/delete/{exerciseId}")
    public ResponseEntity<String> deleteExercise(@PathVariable int exerciseId)
    {
        try
        {
            trainingService.deleteExercise(exerciseId);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Exercise deleted successfully");
    }

    ////////////////////////////////////////////////////////////////////

    // login, email, password xd
    // walidacja stringow - nic nie wyrzuca exeption
    // porownanie do nulli tak samo
    // Maciej: zrobione
    // dziala, ale dziwne te exeption troche bo wyskakuje takie cos jak recipients
    // "brak" a widze ze w metodzie fajnie wyrzuciles wiadomosc wec nwm co tu sie
    // zadzialo:
    // 2025-10-20T14:22:22.218Z WARN 1 --- [snackTrack] [nio-8080-exec-8]
    // .w.s.m.s.DefaultHandlerExceptionResolver : Resolved
    // [org.springframework.http.converter.HttpMessageNotReadableException: JSON
    // parse error: Cannot deserialize value of type
    // `study.snacktrack.entities.enums.Recipients` from String "brak": not one of
    // the values accepted for Enum class: [all, non_premium, premium]]

    //dziala
    @PostMapping("/notifications/add")
    public ResponseEntity<String> createNotification(@RequestBody NotificationRequest request, @RequestHeader("Authorization") String authHeader)
    {
        try
        {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            Admin admin = adminRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

            notificationService.createNotification(request, admin);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Notification created successfully");
    }

    // pola nie moga byc nullami
    // MAciej: już nie będą bo nie da się zapisać nulla ale trzeba dodać response
    // żeby nie pokazywało autora
    // bartek: zrobione
    //dziala
    @GetMapping("/notifications")
    public ResponseEntity<?> getAllNotifications(@RequestHeader("Authorization") String authHeader)
    {
        List<NotificationResponse> notifications;
        try
        {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            adminRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

            notifications = notificationService.getAllNotificationsDetails();
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(notifications);
    }

    // recipients=jvjhvbm musi cos wyrzucac
    // Maciej: wyrzuca już
    // tutaj tez mozna zastosowac NotificationResponse tak jak wyzej
    // tak zrobie i zakomentuje to sie zapoznasz
    // dziala
    // @GetMapping("/notifications/filter")
    // public ResponseEntity<List<Notification>> getNotificationsByRecipients(
    // @RequestHeader("Authorization") String authHeader,
    // @RequestParam Recipients recipients) {
    // String token = authHeader.replace("Bearer ", "");
    // String email = jwtService.extractEmail(token);
    // adminRepository.findByEmail(email)
    // .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

    // List<Notification> notifications =
    // notificationService.getNotificationsByRecipients(recipients);
    // return ResponseEntity.ok(notifications);
    // }

    // druga wersja jak chcesz
    //dziala
    @GetMapping("/notifications/filter")
    public ResponseEntity<?> getNotificationsByRecipients2(@RequestHeader("Authorization") String authHeader, @RequestParam Recipients recipients)
    {
        List<NotificationResponse> notifications;
        try
        {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            adminRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

            notifications = notificationService
                    .getNotificationsByRecipientsWithoutDetails(recipients);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(notifications);
    }

    ////////////////////////////////////////////////////////////////////

    //dziala
    @GetMapping("/comments/reports/comment/{commentId}")
    public ResponseEntity<?> getAllReportsByComment(@PathVariable int commentId)
    {
        List<ReportedComment> reports;
        try
        {
            Comment comment = commentService.getCommentById(commentId);
            reports = reportedCommentService.getAllReportsByComment(comment.getId());
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(reports);
    }

    //dziala
    @GetMapping("/meals/reports/meal/{mealId}")
    public ResponseEntity<?> getAllReportsByMeal(@PathVariable int mealId)
    {
        List<ReportedMeal> reports;
        try
        {
            reports = reportedMealService.getAllReportsByMeal(mealId);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(reports);
    }

    //dziala
    @GetMapping("/comments/reports/all/{reportingId}")
    public ResponseEntity<?> getAllReportedCommentsByUser(@PathVariable int reportingId)
    {
        List<ReportedComment> reportedComments;
        try
        {
            reportedComments = reportedCommentService.getAllReportsByUser(reportingId);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(reportedComments);
    }

    //dziala
    @GetMapping("/meals/reports/all/{reportingId}")
    public ResponseEntity<?> getAllReportedMealsByUser(@PathVariable int reportingId)
    {
        List<ReportedMeal> reportedComments;
        try
        {
            reportedComments = reportedMealService.getAllReportsByUser(reportingId);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(reportedComments);
    }
}
