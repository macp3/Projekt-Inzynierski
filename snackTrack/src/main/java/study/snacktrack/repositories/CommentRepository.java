package study.snacktrack.repositories;

import java.util.List;
import java.util.Optional;

import study.snacktrack.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Comment entities.
 * This extends JpaRepository to handle standard persistence operations and custom queries related to comments on meals.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    /**
     * Retrieves all comments associated with a specific meal ID.
     * This is typically used to display all discussion entries related to a particular meal.
     *
     * @param mealId the ID of the meal
     * @return a List of Comment entities associated with the meal
     */
    List<Comment> findByMealId(int mealId);

    /**
     * Finds a single comment made by a specific author on a given meal.
     * This is often used to ensure that a user has only posted one comment per meal.
     *
     * @param mealId the ID of the meal
     * @param userId the ID of the author (user)
     * @return an Optional containing the Comment entity if found
     */
    Optional<Comment> findByMealIdAndAuthorId(int mealId, int userId);

    /**
     * Retrieves all comments that were posted by a specific user.
     * This method is useful for displaying a user's comment history or managing their contributions.
     *
     * @param userId the ID of the author (user)
     * @return a List of Comment entities authored by the user
     */
    List<Comment> findByAuthorId(int userId);

    /**
     * Checks for the existence of a comment posted by a particular author on a specific meal.
     * This optimized query helps prevent duplicate comments from the same user on the same meal.
     *
     * @param mealId the ID of the meal
     * @param authorId the ID of the author (user)
     * @return true if a comment exists with the given combination of meal and author, false otherwise
     */
    boolean existsByMealIdAndAuthorId(int mealId, int authorId);
}