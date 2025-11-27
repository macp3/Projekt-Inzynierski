package study.snacktrack.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import study.snacktrack.entities.VerificationToken;

/**
 * Repository interface for managing VerificationToken entities, used for account activation or password reset processes.
 * This extends JpaRepository to provide standard persistence operations for tokens.
 */
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    /**
     * Retrieves a VerificationToken entity based on the unique token string itself.
     * This is the primary method used to validate a token submitted by a user via an activation link.
     *
     * @param token the unique verification token string
     * @return an Optional containing the VerificationToken entity if found
     */
    Optional<VerificationToken> findByToken(String token);

    /**
     * Retrieves a VerificationToken entity associated with a specific user ID.
     * This method is used to manage and potentially replace any existing active tokens for a given user.
     *
     * @param userId the ID of the user the token belongs to
     * @return an Optional containing the VerificationToken entity if one exists for the user
     */
    Optional<VerificationToken> findByUserId(int userId);
}