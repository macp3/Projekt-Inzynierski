package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.dto.ReportedCommentResponse;
import com.example.demo.entities.Comment;
import com.example.demo.entities.ReportedComment;
import com.example.demo.entities.User;
import com.example.demo.repositories.CommentRepository;
import com.example.demo.repositories.MealRepository;
import com.example.demo.repositories.ReportedCommentRepository;
import com.example.demo.repositories.ReportedMealRepository;
import com.example.demo.repositories.UserRepository;

@Service
public class ReportedCommentService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ReportedMealRepository reportedMealRepository;
    private final ReportedCommentRepository reportedCommentRepository;

    public ReportedCommentService(UserRepository userRepository, MealRepository mealRepository, CommentRepository commentRepository, ReportedMealRepository reportedMealRepository, ReportedCommentRepository reportedCommentRepository) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.reportedMealRepository = reportedMealRepository;
        this.reportedCommentRepository = reportedCommentRepository;
    }

    private Comment validateCommentExistance(int commentId) {
        if (commentId <= 0) {
            throw new IllegalArgumentException("Comment ID must be greater than zero");
        }

        Optional<Comment> optionalComment = commentRepository.findById(commentId);

        if (optionalComment.isEmpty()) {
            throw new IllegalArgumentException("Comment with specified ID doesn't exist");
        }

        return optionalComment.get();
    }

    public ReportedCommentResponse reportComment(int commentId, int userId, String content) {
        Comment comment = validateCommentExistance(commentId);
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content must not be empty");
        }
        if (comment.getAuthorId() == user.getId()) {
            throw new IllegalArgumentException("You cannot report your own comment");
        }

        reportedCommentRepository.findByCommentIdAndReportingId(comment.getId(), user.getId())
                .ifPresent(x
                        -> {
                    throw new IllegalArgumentException("You have already reported this comment");
                }
                );

        ReportedComment reportedComment = new ReportedComment();
        reportedComment.setCommentId(comment.getId());
        reportedComment.setReportingId(user.getId());
        reportedComment.setContent(content);

        reportedCommentRepository.save(reportedComment);
        return new ReportedCommentResponse(reportedComment.getId(), reportedComment.getReportingId(), reportedComment.getCommentId(), reportedComment.getContent());
    }

    //admin
    public List<ReportedComment> getAllReportsByUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("There is no user with specified ID"));
        List<ReportedComment> reports = reportedCommentRepository.findAllReportsByReportingId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("This user has not reported any meal yet"));
        return reports;
    }

    //admin
    public List<ReportedComment> getAllReportsByComment(int commentId) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find the comment with specified ID"));

        List<ReportedComment> reports = reportedCommentRepository.findAllReportsByCommentId(commentId)
                .orElseThrow(() -> new IllegalArgumentException("This comment has no reports"));
        return reports;
    }
}
