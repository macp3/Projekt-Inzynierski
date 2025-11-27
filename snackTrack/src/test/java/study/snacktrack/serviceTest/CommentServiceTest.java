package study.snacktrack.serviceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import study.snacktrack.repositories.*;
import study.snacktrack.dto.*;
import study.snacktrack.services.*;
import study.snacktrack.entities.*;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the CommentService, covering core business logic related to creating, editing, liking, and deleting comments.
 * This class ensures that the service methods correctly interact with repositories and enforce business rules.
 */
class CommentServiceTest {

    private UserRepository userRepository;
    private MealRepository mealRepository;
    private CommentRepository commentRepository;
    private CommentLikeRepository commentLikeRepository;
    private CommentService commentService;

    /**
     * Sets up mock repositories and initializes the CommentService instance before each test.
     * This isolates the service logic, allowing for verification of correct repository method calls.
     */
    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        mealRepository = mock(MealRepository.class);
        commentRepository = mock(CommentRepository.class);
        commentLikeRepository = mock(CommentLikeRepository.class);

        commentService = new CommentService(
                userRepository, mealRepository, commentRepository, commentLikeRepository);
    }

    /**
     * Tests the successful addition of a new comment to a meal.
     * It verifies that the service saves the new comment entity and returns the correctly mapped {@code CommentResponse} DTO.
     */
    @Test
    void addCommentToMeal_shouldSaveAndReturnResponse() {
        User user = new User();
        user.setId(1);
        user.setName("John");
        user.setSurname("Doe");
        user.setImageUrl("img.png");

        Meal meal = new Meal();
        meal.setId(10);
        meal.setName("Pizza");
        meal.setDescription("Cheese pizza");

        CommentRequest request = new CommentRequest();
        request.setMealId(10);
        request.setContent("Great meal!");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(mealRepository.findById(10)).thenReturn(Optional.of(meal));
        when(commentRepository.existsByMealIdAndAuthorId(10, 1)).thenReturn(false);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment c = invocation.getArgument(0);
            c.setId(100);
            return c;
        });

        CommentResponse response = commentService.addCommentToMeal(1, request);

        assertEquals(100, response.getId());
        assertEquals("John Doe", response.getAuthorName());
        assertEquals("img.png", response.getAuthorImageUrl());
        assertEquals("Great meal!", response.getContent());

        verify(commentRepository).save(any(Comment.class));
    }

    /**
     * Tests the successful update of an existing comment's content.
     * It verifies that the service updates the entity, saves the changes, and correctly maps the result to a {@code CommentResponse}.
     */
    @Test
    void editComment_shouldUpdateContentAndReturnResponse() {
        User user = new User();
        user.setId(1);
        user.setName("John");
        user.setSurname("Doe");
        user.setImageUrl("img.png");

        Comment comment = new Comment();
        comment.setId(200);
        comment.setAuthorId(1);
        comment.setMealId(10);
        comment.setContent("Old content");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(commentRepository.findById(200)).thenReturn(Optional.of(comment));
        when(commentLikeRepository.existsByUserIdAndCommentId(1, 200)).thenReturn(true);

        CommentResponse response = commentService.editComment(1, 200, "New content");

        assertEquals("New content", response.getContent());
        assertTrue(response.isLiked());
        assertEquals("John Doe", response.getAuthorName());

        verify(commentRepository).save(comment);
    }

    /**
     * Tests the logic for adding a new like when the user has not liked the comment previously.
     * It verifies that the like count is incremented and a new {@code CommentLike} entity is saved.
     */
    @Test
    void toggleLike_shouldAddLikeWhenNotExists() {
        Comment comment = new Comment();
        comment.setId(300);
        comment.setLikes(0);

        when(commentRepository.findById(300)).thenReturn(Optional.of(comment));
        when(commentLikeRepository.findByUserIdAndCommentId(1, 300)).thenReturn(Optional.empty());

        commentService.toggleLike(1, 300);

        assertEquals(1, comment.getLikes());
        verify(commentLikeRepository).save(any(CommentLike.class));
        verify(commentRepository).save(comment);
    }

    /**
     * Tests the logic for removing an existing like when the user has already liked the comment.
     * It verifies that the like count is decremented and the existing {@code CommentLike} entity is deleted.
     */
    @Test
    void toggleLike_shouldRemoveLikeWhenExists() {
        Comment comment = new Comment();
        comment.setId(400);
        comment.setLikes(2);

        CommentLike like = new CommentLike(1, comment);

        when(commentRepository.findById(400)).thenReturn(Optional.of(comment));
        when(commentLikeRepository.findByUserIdAndCommentId(1, 400)).thenReturn(Optional.of(like));

        commentService.toggleLike(1, 400);

        assertEquals(1, comment.getLikes());
        verify(commentLikeRepository).delete(like);
        verify(commentRepository).save(comment);
    }

    /**
     * Tests the successful deletion of a comment by its author, identified by meal ID and author ID.
     * It verifies that the service finds the correct comment and performs the delete operation.
     */
    @Test
    void deleteCommentByUser_shouldDeleteWhenAuthorized() {
        Comment comment = new Comment();
        comment.setId(500);
        comment.setAuthorId(1);
        comment.setMealId(10);

        when(commentRepository.findByMealIdAndAuthorId(10, 1)).thenReturn(Optional.of(comment));

        commentService.deleteCommentByUser(1, 10);

        verify(commentRepository).delete(comment);
    }
}