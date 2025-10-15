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

import study.snacktrack.dto.ReportedCommentRequest;
import study.snacktrack.dto.ReportedCommentResponse;
import study.snacktrack.entities.Comment;
import study.snacktrack.entities.ReportedComment;
import study.snacktrack.entities.User;
import study.snacktrack.services.CommentService;
import study.snacktrack.services.ReportedCommentService;

@RestController
@RequestMapping("/comments/reports")
public class ReportedCommentController {

    private final CommentService commentService;
    private final UserService userService;
    private final JwtService jwtService;
    private final ReportedCommentService reportedCommentService;

    public ReportedCommentController(CommentService commentService, UserService userService, JwtService jwtService, ReportedCommentService reportedCommentService) {
        this.commentService = commentService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.reportedCommentService = reportedCommentService;
    }

    private User authorizeUser(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email);
        return user;
    }

    @PostMapping("/add")
    public ResponseEntity<ReportedCommentResponse> reportComment(@RequestBody ReportedCommentRequest request, @RequestHeader("Authorization") String authHeader) {
        User user = authorizeUser(authHeader);

        ReportedCommentResponse response = reportedCommentService.reportComment(request.getCommentId(), user.getId(), request.getContent());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<List<ReportedComment>> getAllReportsByComment(@PathVariable int commentId) {
        Comment comment = commentService.getCommentById(commentId);
        List<ReportedComment> reports = reportedCommentService.getAllReportsByComment(comment.getId());
        return ResponseEntity.ok(reports);
    }
}
