package study.snacktrack.controllers;

import java.util.List;

import study.snacktrack.services.JwtService;
import study.snacktrack.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import study.snacktrack.dto.ReportedMealRequest;
import study.snacktrack.dto.ReportedMealResponse;
import study.snacktrack.entities.ReportedMeal;
import study.snacktrack.entities.User;
import study.snacktrack.services.CommentService;
import study.snacktrack.services.MealService;
import study.snacktrack.services.ReportedMealService;

@RestController
@RequestMapping("/meals/reports")
public class ReportedMealController {

    private final JwtService jwtService;
    private final UserService userService;
    private final ReportedMealService reportedMealService;

    public ReportedMealController(MealService mealService, JwtService jwtService, UserService userService, CommentService commentService, ReportedMealService reportedMealService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.reportedMealService = reportedMealService;
    }

    private User authorizeUser(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email);
        return user;
    }

    //dzialaa
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
