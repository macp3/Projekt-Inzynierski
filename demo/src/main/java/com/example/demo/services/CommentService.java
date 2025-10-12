package com.example.demo.services;

import com.example.demo.dto.CommentRequest;
import com.example.demo.dto.CommentResponse;
import com.example.demo.entities.Comment;
import com.example.demo.entities.Meal;
import com.example.demo.entities.User;
import com.example.demo.repositories.CommentRepository;
import com.example.demo.repositories.MealRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService
{
    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final CommentRepository commentRepository;

    public CommentService(UserRepository userRepository, MealRepository mealRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.commentRepository = commentRepository;
    }

    private Comment validateCommentExistance(int commentId)
    {
        if (commentId <= 0) {
            throw new IllegalArgumentException("Comment ID must be greater than zero");
        }

        Optional<Comment> optionalComment = commentRepository.findById(commentId);

        if (optionalComment.isEmpty()) {
            throw new IllegalArgumentException("Comment with specified ID doesn't exist");
        }

        return optionalComment.get();
    }
    public CommentResponse addCommentToMeal(int authorId, CommentRequest request)
    {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Meal meal = mealRepository.findById(request.getMealId())
                .orElseThrow(() -> new IllegalArgumentException("Meal not found"));

        if(request.getContent() == null || request.getContent().isBlank())
            throw new IllegalArgumentException("Content must not be empty");

        if(commentRepository.existsByMealIdAndAuthorId(meal.getId(), author.getId())) {
            throw new IllegalArgumentException("You have already commented this meal");
        }

        Comment comment = new Comment();
        comment.setAuthorId(author.getId());
        comment.setMealId(meal.getId());
        comment.setContent(request.getContent());
        commentRepository.save(comment);

        return new CommentResponse(comment.getId(), author.getId(), comment.getContent(), meal.getId());
    }

    public CommentResponse editComment(int authorId, int commentId, String content)
    {
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("There is no user with specified ID"));

        Comment comment = validateCommentExistance(commentId);

        if(user.getId() != comment.getAuthorId())
            throw new IllegalArgumentException("You are not authorized to edit this comment");

        if(content == null || content.isBlank())
            throw new IllegalArgumentException("Content must not be empty");

        CommentResponse response = new CommentResponse(commentId, authorId, content, comment.getMealId());
        comment.setContent(response.getContent());
        commentRepository.save(comment);
        return response;
    }

    public void deleteCommentByUser(int authorId, int commentId)
    {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if(comment.getAuthorId() != authorId)
            throw new IllegalArgumentException("You are not authorized to delete this comment");

        commentRepository.delete(comment);
    }

    //xddddd ale glupota
    //NIE WIEM GDZIE TEGO UZYC - CZY W COMMENTS CZY W MEALS
    //narazie uzylem w mealcontrollerze na dole
    public List<Comment> getAllMealComments(int mealId)
    {
        if(!mealRepository.existsById(mealId))
            throw new IllegalArgumentException("Meal not found");

        return commentRepository.findByMealId(mealId);
    }

    public List<CommentResponse> getAllCommentsByUser(int userId) {
        if(!userRepository.existsById(userId))
            throw new IllegalArgumentException("User not found");

        return commentRepository.findByAuthorId(userId)
                .stream()
                .map(c -> new CommentResponse(c.getId(), c.getAuthorId(), c.getContent(), c.getMealId()))
                .toList();
    }

    //zwraca komentarz uzytkownika pod wybranym mealem
    //funkcja potrzebna do edycji i usuwania :)))))))))
    //ale mi sie chce jarać oezuuuuuu
    public Comment getUserMealComment(int mealId, int userId)
    {
        return commentRepository.findByMealIdAndAuthorId(mealId, userId)
                .orElseThrow(() -> new IllegalArgumentException("This user has no registered comment for this meal"));
    }

    public Comment getCommentById(int commentId)
    {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("There is no comment with specified ID"));
    }
}
