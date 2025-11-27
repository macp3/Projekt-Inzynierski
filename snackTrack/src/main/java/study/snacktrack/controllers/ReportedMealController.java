package study.snacktrack.controllers;

import study.snacktrack.services.JwtService;
import study.snacktrack.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study.snacktrack.dto.ReportedMealRequest;
import study.snacktrack.dto.ReportedMealResponse;
import study.snacktrack.entities.User;
import study.snacktrack.services.ReportedMealService;

/**
 * REST controller for handling user actions related to reporting meals.
 * All reported meals are sent for administrative review.
 */
@RestController
@RequestMapping("/meals/reports")
public class ReportedMealController {

    /** Service for JWT token handling. */
    private final JwtService jwtService;
    /** Service for user data access and retrieval. */
    private final UserService userService;
    /** Service for business logic related to reported meals. */
    private final ReportedMealService reportedMealService;

    /**
     * Constructs the ReportedMealController with required dependencies.
     * Note: MealService and CommentService are injected but not used directly in the controller methods.
     */
    public ReportedMealController(JwtService jwtService, UserService userService, ReportedMealService reportedMealService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.reportedMealService = reportedMealService;
    }

    /**
     * Extracts the JWT from the Authorization header, validates it, and retrieves the corresponding User entity.
     *
     * @param authHeader The Authorization header containing the Bearer token.
     * @return The authenticated User entity.
     */
    private User authorizeUser(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email);
        return user;
    }

    /**
     * Registers a new report against a specified meal.
     *
     * @param request The ReportedMealRequest DTO containing the meal ID and reason.
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity with the ReportedMealResponse DTO or a bad request error.
     */
    @PostMapping("/add")
    public ResponseEntity<?> reportMeal(@RequestBody ReportedMealRequest request, @RequestHeader("Authorization") String authHeader)
    {
        ReportedMealResponse response;
        try
        {
            User user = authorizeUser(authHeader);

            response = reportedMealService.reportMeal(request.getMealId(), user.getId(), request.getContent());
        }
        catch(IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}