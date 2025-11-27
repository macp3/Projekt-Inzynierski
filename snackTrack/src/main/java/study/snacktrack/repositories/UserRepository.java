package study.snacktrack.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import study.snacktrack.entities.User;
import study.snacktrack.entities.enums.Status;

/**
 * Repository interface for managing User entities.
 * This extends JpaRepository to provide standard persistence and custom methods for user lookup and filtering.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Checks if a user already exists with the given email address.
     * This method is crucial for registration validation to ensure email uniqueness.
     *
     * @param email the email address to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Retrieves a user entity based on their unique email address.
     * This is the primary method used for user login and fetching user details.
     *
     * @param email the email address of the user
     * @return an Optional containing the User entity or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Retrieves a list of all users who currently have an active premium subscription.
     * This is inferred by checking for a non-null premium expiration date in the database.
     *
     * @return a List of User entities with active premium subscriptions
     */
    List<User> findByPremiumExpirationNotNull();

    /**
     * Retrieves a list of all users who do not have an active premium subscription.
     * This is inferred by checking for a null premium expiration date in the database.
     *
     * @return a List of User entities without active premium subscriptions
     */
    List<User> findByPremiumExpirationNull();

    /**
     * Retrieves all User entities using pagination parameters.
     * This method allows for efficient fetching of the entire user base in manageable chunks.
     *
     * @param pageable the pagination information (page number, size, sort)
     * @return a Page of User entities
     */
    Page<User> findAll(Pageable pageable);

    /**
     * Performs a case-insensitive search across user email, name, or surname with pagination support.
     * This custom query is used by administrators to quickly find specific users within the system.
     *
     * @param query the search string to match against email, name, or surname
     * @param pageable the pagination information
     * @return a Page of User entities matching the search query
     */
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.surname) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<User> searchUsers(String query, Pageable pageable);

    /**
     * Counts the total number of users who currently have the specified status.
     * This method is used for administrative statistics, such as determining the number of active or banned users.
     *
     * @param status the status to count (e.g., active, banned)
     * @return the total count of users with the given status
     */
    long countByStatus(Status status);

    /**
     * Counts the number of users whose premium subscription expires after the given date.
     * This helps track users who will still have premium access beyond a specified future date.
     *
     * @param date the date to compare the premium expiration against
     * @return the total count of users with premium expiring after the given date
     */
    long countByPremiumExpirationAfter(LocalDate date);
}