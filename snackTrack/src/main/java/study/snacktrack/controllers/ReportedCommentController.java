package study.snacktrack.controllers;

import study.snacktrack.services.JwtService;
import study.snacktrack.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study.snacktrack.dto.ReportedCommentRequest;
import study.snacktrack.dto.ReportedCommentResponse;
import study.snacktrack.entities.User;
import study.snacktrack.services.ReportedCommentService;

/**
 * REST controller for handling user actions related to reporting comments.
 * All reported comments are sent for administrative review.
 */
@RestController
@RequestMapping("/comments/reports")
public class ReportedCommentController {

    /** Service for user data access and retrieval. */
    private final UserService userService;
    /** Service for JWT token handling. */
    private final JwtService jwtService;
    /** Service for business logic related to reported comments. */
    private final ReportedCommentService reportedCommentService;

    /**
     * Constructs the ReportedCommentController with required dependencies.
     */
    public ReportedCommentController(UserService userService, JwtService jwtService, ReportedCommentService reportedCommentService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.reportedCommentService = reportedCommentService;
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
     * Registers a new report against a specified comment.
     *
     * @param request The ReportedCommentRequest DTO containing the comment ID and reason.
     * @param authHeader The Authorization header for user identification.
     * @return ResponseEntity with the ReportedCommentResponse DTO or a bad request error.
     */
    @PostMapping("/add")
    public ResponseEntity<?> reportComment(@RequestBody ReportedCommentRequest request, @RequestHeader("Authorization") String authHeader)
    {
        ReportedCommentResponse response;
        try
        {
            User user = authorizeUser(authHeader);

            response = reportedCommentService.reportComment(request.getCommentId(), user.getId(), request.getContent());
        }
        catch(IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}