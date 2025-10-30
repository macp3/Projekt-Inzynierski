package study.snacktrack.controllers;

import java.util.ArrayList;
import java.util.List;

import study.snacktrack.dto.CommentRequest;
import study.snacktrack.dto.CommentResponse;
import study.snacktrack.entities.Comment;
import study.snacktrack.services.CommentService;
import study.snacktrack.services.JwtService;
import study.snacktrack.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import study.snacktrack.entities.User;
import study.snacktrack.services.MealService;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final JwtService jwtService;
    private final UserService userService;
    private final CommentService commentService;

    public CommentController(MealService mealService, JwtService jwtService, UserService userService,
            CommentService commentService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.commentService = commentService;
    }

    private User authorizeUser(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email);
        return user;
    }

    //dziala
    @PostMapping("/add")
    public ResponseEntity<?> addCommentToMeal(@RequestBody CommentRequest request, @RequestHeader("Authorization") String authHeader)
    {
        CommentResponse response;
        try
        {
            User user = authorizeUser(authHeader);

            response = commentService.addCommentToMeal(user.getId(), request);
        }
        catch(IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    //dziala
    @PutMapping("/edit")
    public ResponseEntity<?> editCommentByUser(@RequestBody CommentRequest request,
            @RequestHeader("Authorization") String authHeader)
    {
        CommentResponse cr;
        try
        {
            User user = authorizeUser(authHeader);
            Comment comment = commentService.getUserMealComment(request.getMealId(), user.getId());

            cr = commentService.editComment(user.getId(), comment.getId(), request.getContent());
        }
        catch(IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(cr);
    }

    //dziala
    @GetMapping("/my")
    public ResponseEntity<?> getAllCommentsByUser(
            @RequestHeader("Authorization") String authHeader)
    {
        List<CommentResponse> response;
        try
        {
            User user = authorizeUser(authHeader);
            response = commentService.getAllCommentsByUser(user.getId());
        }
        catch(IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    // zmiana - nie bedzie comment id tylko meal id - nie wiem co ja tu zrobilem
    //dziala
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCommentByUser(
            @RequestParam int mealId,
            @RequestHeader("Authorization") String authHeader)
    {
        try
        {
            User user = authorizeUser(authHeader);
            commentService.deleteCommentByUser(user.getId(), mealId);
        }
        catch(IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Comment deleted successfully");
    }

    //dziala
    @GetMapping("/meal/{mealId}")
    public ResponseEntity<?> getAllCommentsForMeal(@PathVariable int mealId)
    {
        List<CommentResponse> response;
        try
        {
            List<Comment> comments = commentService.getAllMealComments(mealId);
            response = new ArrayList<>();

            for (Comment c : comments) {
                CommentResponse cr = new CommentResponse(c.getId(), c.getAuthorId(), c.getContent(), c.getMealId());
                response.add(cr);
            }
        }
        catch(IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}
