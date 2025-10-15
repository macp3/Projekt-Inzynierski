package study.snacktrack.repositories;

import java.util.List;
import java.util.Optional;

import study.snacktrack.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    public List<Comment> findByMealId(int mealId);

    Optional<Comment> findByMealIdAndAuthorId(int mealId, int userId);

    List<Comment> findByAuthorId(int userId);

    boolean existsByMealIdAndAuthorId(int mealId, int authorId);
}
