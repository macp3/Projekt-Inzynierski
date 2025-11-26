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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentControllerTest {

    private JwtService jwtService;
    private UserService userService;
    private CommentService commentService;
    private CommentController controller;

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

    @Test
    void getAllCommentsByUser_shouldReturnList() {
        when(commentService.getAllCommentsByUser(1))
                .thenReturn(List.of(new CommentResponse(1, 1, "John Doe", "img.png", "Nice", 10, 2, true)));


        ResponseEntity<?> response = controller.getAllCommentsByUser("Bearer valid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(commentService).getAllCommentsByUser(1);
    }

    @Test
    void deleteCommentByUser_shouldReturnOk() {
        ResponseEntity<String> response = controller.deleteCommentByUser(10, "Bearer valid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Comment deleted successfully", response.getBody());
        verify(commentService).deleteCommentByUser(1, 10);
    }

    @Test
    void getAllCommentsForMeal_shouldReturnList() {
        when(commentService.getAllMealComments(10, 1))
                .thenReturn(List.of(new CommentResponse(2, 1, "John Doe", "img.png", "Meal comment", 10, 1, false)));


        ResponseEntity<?> response = controller.getAllCommentsForMeal(10, "Bearer valid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(commentService).getAllMealComments(10, 1);
    }

    @Test
    void toggleLike_shouldReturnOk() {
        ResponseEntity<String> response = controller.toggleLike(5, "Bearer valid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Like toggled", response.getBody());
        verify(commentService).toggleLike(1, 5);
    }
}
