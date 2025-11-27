package study.snacktrack.services;

import java.util.List;
import java.util.Optional;

import study.snacktrack.dto.ReportedCommentResponse;
import study.snacktrack.entities.Comment;
import study.snacktrack.repositories.UserRepository;
import org.springframework.stereotype.Service;

import study.snacktrack.entities.ReportedComment;
import study.snacktrack.entities.User;
import study.snacktrack.repositories.CommentRepository;
import study.snacktrack.repositories.ReportedCommentRepository;

/**
 * Service responsible for managing reported comments.
 * Provides functionality for reporting, retrieving, and deleting comment reports.
 */
@Service
public class ReportedCommentService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ReportedCommentRepository reportedCommentRepository;

    /**
     * Constructs ReportedCommentService with required repositories.
     */
    public ReportedCommentService(UserRepository userRepository,
                                  CommentRepository commentRepository,
                                  ReportedCommentRepository reportedCommentRepository) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.reportedCommentRepository = reportedCommentRepository;
    }

    /**
     * Validates if a comment exists by ID.
     *
     * @param commentId comment identifier
     * @return Comment entity if found
     */
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

    /**
     * Reports a comment by a user with provided content.
     *
     * @param commentId comment identifier
     * @param userId reporting user identifier
     * @param content report content
     * @return response DTO with report details
     */
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
                .ifPresent(x -> {
                    throw new IllegalArgumentException("You have already reported this comment");
                });

        ReportedComment reportedComment = new ReportedComment();
        reportedComment.setCommentId(comment.getId());
        reportedComment.setReportingId(user.getId());
        reportedComment.setContent(content);

        reportedCommentRepository.save(reportedComment);
        return new ReportedCommentResponse(reportedComment.getId(), reportedComment.getReportingId(),
                reportedComment.getCommentId(), reportedComment.getContent());
    }

    /**
     * Retrieves all reports created by a specific user.
     *
     * @param userId user identifier
     * @return list of reported comments
     */
    public List<ReportedComment> getAllReportsByUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("There is no user with specified ID"));
        List<ReportedComment> reports = reportedCommentRepository.findAllByReportingId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("This user has not reported any meal yet"));
        return reports;
    }

    /**
     * Retrieves all reports for a specific comment.
     *
     * @param commentId comment identifier
     * @return list of reported comments
     */
    public List<ReportedComment> getAllReportsByComment(int commentId) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find the comment with specified ID"));

        List<ReportedComment> reports = reportedCommentRepository.findAllByCommentId(commentId)
                .orElseThrow(() -> new IllegalArgumentException("This comment has no reports"));
        return reports;
    }

    /**
     * Retrieves all reported comments in the system.
     *
     * @return list of reported comments
     */
    public List<ReportedComment> getAllReports() {
        return reportedCommentRepository.findAll();
    }

    /**
     * Deletes a reported comment by its report ID.
     *
     * @param reportId report identifier
     */
    public void deleteReport(int reportId) {
        if (reportId <= 0) {
            throw new IllegalArgumentException("Report ID must be greater than zero");
        }

        ReportedComment report = reportedCommentRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report with ID " + reportId + " not found"));

        reportedCommentRepository.delete(report);
    }
}
