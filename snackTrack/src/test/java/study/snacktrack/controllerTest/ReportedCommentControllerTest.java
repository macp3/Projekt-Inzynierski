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

class ReportedCommentControllerTest {

    private CommentService commentService;
    private UserService userService;
    private JwtService jwtService;
    private ReportedCommentService reportedCommentService;
    private ReportedCommentController controller;

    @BeforeEach
    void setUp() {
        commentService = mock(CommentService.class);
        userService = mock(UserService.class);
        jwtService = mock(JwtService.class);
        reportedCommentService = mock(ReportedCommentService.class);

        controller = new ReportedCommentController(commentService, userService, jwtService, reportedCommentService);

        // Stub autoryzacji
        when(jwtService.extractEmail("token")).thenReturn("user@example.com");
        User user = new User();
        user.setId(1);
        user.setEmail("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(user);
    }

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
