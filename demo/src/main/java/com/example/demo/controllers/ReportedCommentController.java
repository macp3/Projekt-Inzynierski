package com.example.demo.controllers;

import com.example.demo.dto.ReportedCommentRequest;
import com.example.demo.dto.ReportedCommentResponse;
import com.example.demo.entities.Comment;
import com.example.demo.entities.ReportedComment;
import com.example.demo.entities.User;
import com.example.demo.services.CommentService;
import com.example.demo.services.JwtService;
import com.example.demo.services.ReportedCommentService;
import com.example.demo.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments/reports")
public class ReportedCommentController
{
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

    private User authorizeUser(String authHeader)
    {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userService.getUserByEmail(email);
        return user;
    }

    @PostMapping("/add")
    public ResponseEntity<ReportedCommentResponse> reportComment(@RequestBody ReportedCommentRequest request, @RequestHeader ("Authorization") String authHeader)
    {
        User user = authorizeUser(authHeader);
        Comment comment = commentService.getCommentById(request.getCommentId());

        ReportedCommentResponse response = reportedCommentService.reportComment(request.getCommentId(), user.getId(), request.getContent());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<List<ReportedComment>> getAllReportsByComment(@PathVariable int commentId)
    {
        Comment comment = commentService.getCommentById(commentId);
        List<ReportedComment> reports = reportedCommentService.getAllReportsByComment(comment.getId());
        return ResponseEntity.ok(reports);
    }
}
