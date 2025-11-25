package study.snacktrack.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import study.snacktrack.entities.CommentLike;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Integer> {
    Optional<CommentLike> findByUserIdAndCommentId(int userId, int commentId);

    boolean existsByUserIdAndCommentId(int userId, int commentId);
}