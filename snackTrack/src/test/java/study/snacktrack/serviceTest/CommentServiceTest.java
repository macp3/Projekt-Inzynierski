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

class CommentServiceTest {

    private UserRepository userRepository;
    private MealRepository mealRepository;
    private CommentRepository commentRepository;
    private CommentLikeRepository commentLikeRepository;
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        mealRepository = mock(MealRepository.class);
        commentRepository = mock(CommentRepository.class);
        commentLikeRepository = mock(CommentLikeRepository.class);

        commentService = new CommentService(
                userRepository, mealRepository, commentRepository, commentLikeRepository);
    }

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
