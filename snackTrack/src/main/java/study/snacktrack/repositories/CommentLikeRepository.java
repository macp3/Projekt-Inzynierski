package study.snacktrack.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import study.snacktrack.entities.CommentLike;
import java.util.Optional;

/**
 * Repository interface for managing CommentLike entities, representing user likes on comments.
 * It provides standard database operations along with custom methods for checking specific like relationships.
 */
public interface CommentLikeRepository extends JpaRepository<CommentLike, Integer> {

    /**
     * Retrieves a CommentLike entity based on the specific user and comment IDs.
     * This method is used to find an existing like record for a user on a particular comment, typically before deletion.
     *
     * @param userId the ID of the user who liked the comment
     * @param commentId the ID of the comment that was liked
     * @return an Optional containing the CommentLike entity or empty if the like doesn't exist
     */
    Optional<CommentLike> findByUserIdAndCommentId(int userId, int commentId);

    /**
     * Checks if a like record exists for a given user on a specific comment.
     * This boolean check is highly efficient for determining if a user has already liked the comment.
     *
     * @param userId the ID of the user to check
     * @param commentId the ID of the comment to check
     * @return true if a like exists, false otherwise
     */
    boolean existsByUserIdAndCommentId(int userId, int commentId);
}