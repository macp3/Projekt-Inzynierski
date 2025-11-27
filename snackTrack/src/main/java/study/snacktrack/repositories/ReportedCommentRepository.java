package study.snacktrack.repositories;

import java.util.List;
import java.util.Optional;

import study.snacktrack.entities.ReportedComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing ReportedComment entities, which track user reports against comments.
 * This extends JpaRepository to handle standard persistence operations and specialized lookups related to reports.
 */
@Repository
public interface ReportedCommentRepository extends JpaRepository<ReportedComment, Integer> {

    /**
     * Retrieves a specific report entry based on the comment ID and the ID of the user who filed the report.
     * This is crucial for checking if a specific user has already reported a given comment.
     *
     * @param commentId the ID of the reported comment
     * @param reportingId the ID of the user who filed the report
     * @return an Optional containing the ReportedComment entity if the specific report exists
     */
    Optional<ReportedComment> findByCommentIdAndReportingId(int commentId, int reportingId);

    /**
     * Retrieves all report entries filed by a specific user.
     * This method is useful for listing a user's reporting history or managing their actions.
     *
     * @param reportingId the ID of the user who filed the reports
     * @return an Optional containing a List of ReportedComment entities created by the user
     */
    Optional<List<ReportedComment>> findAllByReportingId(int reportingId);

    /**
     * Retrieves all report entries that have been filed against a specific comment ID.
     * This is essential for administrators to view all reports concerning a single comment, often to decide on moderation action.
     *
     * @param commentId the ID of the reported comment
     * @return an Optional containing a List of ReportedComment entities filed against the comment
     */
    Optional<List<ReportedComment>> findAllByCommentId(int commentId);
}