package study.snacktrack.controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import study.snacktrack.controllers.CommentController;
import study.snacktrack.dto.CommentResponse;
import study.snacktrack.entities.Comment;
import study.snacktrack.entities.User;
import study.snacktrack.dto.CommentRequest;
import study.snacktrack.services.CommentService;
import study.snacktrack.services.JwtService;
import study.snacktrack.services.UserService;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the CommentController, ensuring proper handling of comment creation, editing, deletion, and retrieval.
 * This class isolates the controller logic by mocking its service dependencies and validating correct request delegation and response statuses.
 */
class CommentControllerTest {

    private JwtService jwtService;
    private UserService userService;
    private CommentService commentService;
    private CommentController controller;

    /**
     * Sets up the necessary mock services and initializes the CommentController instance before each test.
     * It also sets up global mock behavior to successfully extract the user ID from the authorization header, simulating authentication.
     */
    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        userService = mock(UserService.class);
        commentService = mock(CommentService.class);
        controller = new CommentController(jwtService, userService, commentService);

        when(jwtService.extractEmail("valid-token")).thenReturn("user@example.com");
        User user = new User();
        user.setId(1);
        user.setEmail("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(user);
    }

    /**
     * Tests the successful addition of a new comment to a meal.
     * It verifies that the controller delegates the request to the {@code CommentService} and returns an HTTP 200 OK status.
     */
    @Test
    void addCommentToMeal_shouldReturnOk() {
        CommentRequest req = new CommentRequest();
        req.setMealId(10);
        req.setContent("Nice meal");

        when(commentService.addCommentToMeal(1, req))
                .thenReturn(new CommentResponse(1, 1, "John Doe", "img.png", "Nice meal", 10, 0, false));


        ResponseEntity<?> response = controller.addCommentToMeal(req, "Bearer valid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(commentService).addCommentToMeal(1, req);
    }

    /**
     * Tests the successful editing of an existing comment by its author.
     * It verifies the correct comment ID is retrieved and the edit operation is delegated to the {@code CommentService}.
     */
    @Test
    void editCommentByUser_shouldReturnOk() {
        CommentRequest req = new CommentRequest();
        req.setMealId(10);
        req.setContent("Edited");

        Comment existing = new Comment();
        existing.setId(5);
        existing.setMealId(10);
        existing.setAuthorId(1);

        when(commentService.getUserMealComment(10, 1)).thenReturn(existing);
        when(commentService.editComment(1, 5, "Edited"))
                .thenReturn(new CommentResponse(5, 1, "John Doe", "img.png", "Edited", 10, 0, false));


        ResponseEntity<?> response = controller.editCommentByUser(req, "Bearer valid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(commentService).editComment(1, 5, "Edited");
    }

    /**
     * Tests the successful retrieval of all comments written by the currently authenticated user.
     * It verifies that the controller delegates to the {@code CommentService} using the authenticated user's ID and returns an HTTP 200 OK status.
     */
    @Test
    void getAllCommentsByUser_shouldReturnList() {
        when(commentService.getAllCommentsByUser(1))
                .thenReturn(List.of(new CommentResponse(1, 1, "John Doe", "img.png", "Nice", 10, 2, true)));


        ResponseEntity<?> response = controller.getAllCommentsByUser("Bearer valid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(commentService).getAllCommentsByUser(1);
    }

    /**
     * Tests the successful deletion of a comment by its author.
     * It verifies that the controller returns an HTTP 200 OK status and ensures the delete operation is performed by the {@code CommentService}.
     */
    @Test
    void deleteCommentByUser_shouldReturnOk() {
        ResponseEntity<String> response = controller.deleteCommentByUser(10, "Bearer valid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Comment deleted successfully", response.getBody());
        verify(commentService).deleteCommentByUser(1, 10);
    }

    /**
     * Tests the successful retrieval of all comments associated with a specific meal ID.
     * It verifies that the controller delegates the request using both the meal ID and the requesting user's ID for like status context.
     */
    @Test
    void getAllCommentsForMeal_shouldReturnList() {
        when(commentService.getAllMealComments(10, 1))
                .thenReturn(List.of(new CommentResponse(2, 1, "John Doe", "img.png", "Meal comment", 10, 1, false)));


        ResponseEntity<?> response = controller.getAllCommentsForMeal(10, "Bearer valid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(commentService).getAllMealComments(10, 1);
    }

    /**
     * Tests the successful toggling (adding or removing) of a like on a specific comment.
     * It verifies the controller delegates the operation to the {@code CommentService} and returns an HTTP 200 OK status.
     */
    @Test
    void toggleLike_shouldReturnOk() {
        ResponseEntity<String> response = controller.toggleLike(5, "Bearer valid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Like toggled", response.getBody());
        verify(commentService).toggleLike(1, 5);
    }
}