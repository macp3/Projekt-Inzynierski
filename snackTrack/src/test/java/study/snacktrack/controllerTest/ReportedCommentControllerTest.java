package study.snacktrack.controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import study.snacktrack.controllers.ReportedCommentController;
import study.snacktrack.dto.ReportedCommentRequest;
import study.snacktrack.dto.ReportedCommentResponse;
import study.snacktrack.entities.User;
import study.snacktrack.services.CommentService;
import study.snacktrack.services.JwtService;
import study.snacktrack.services.ReportedCommentService;
import study.snacktrack.services.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ReportedCommentController, focusing primarily on the functionality for users to report inappropriate comments.
 * This class uses Mockito to isolate the controller logic, verifying correct request delegation and appropriate response statuses.
 */
class ReportedCommentControllerTest {

    private CommentService commentService;
    private UserService userService;
    private JwtService jwtService;
    private ReportedCommentService reportedCommentService;
    private ReportedCommentController controller;

    /**
     * Sets up the necessary mock services and initializes the ReportedCommentController instance before each test.
     * This method also stubs the authentication process to simulate a logged-in user with ID 1 for authorization checks.
     */
    @BeforeEach
    void setUp() {
        commentService = mock(CommentService.class);
        userService = mock(UserService.class);
        jwtService = mock(JwtService.class);
        reportedCommentService = mock(ReportedCommentService.class);

        controller = new ReportedCommentController(userService, jwtService, reportedCommentService);

        when(jwtService.extractEmail("token")).thenReturn("user@example.com");
        User user = new User();
        user.setId(1);
        user.setEmail("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(user);
    }

    /**
     * Tests the successful reporting of a comment by an authenticated user.
     * It verifies that the controller delegates the report request to the {@code ReportedCommentService} and returns the generated response DTO with an HTTP 200 OK status.
     */
    @Test
    void reportComment_shouldReturnOk() {
        ReportedCommentRequest req = new ReportedCommentRequest(5, "Spam content");
        ReportedCommentResponse resp = new ReportedCommentResponse(1, 1, 5, "Spam content");

        when(reportedCommentService.reportComment(5, 1, "Spam content")).thenReturn(resp);

        ResponseEntity<?> response = controller.reportComment(req, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ReportedCommentResponse);
        assertEquals(5, ((ReportedCommentResponse) response.getBody()).getCommentId());
    }

    /**
     * Tests the failure case during comment reporting when a business logic exception occurs (e.g., the comment ID is invalid).
     * It verifies that the controller handles the {@code IllegalArgumentException}, returns an HTTP 400 Bad Request status, and includes the exception message in the response body.
     */
    @Test
    void reportComment_shouldReturnBadRequest_whenException() {
        ReportedCommentRequest req = new ReportedCommentRequest(5, "Spam content");

        when(reportedCommentService.reportComment(5, 1, "Spam content"))
                .thenThrow(new IllegalArgumentException("Invalid comment"));

        ResponseEntity<?> response = controller.reportComment(req, "Bearer token");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid comment", response.getBody());
    }
}