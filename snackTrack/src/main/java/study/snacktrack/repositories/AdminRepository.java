package study.snacktrack.repositories;

import java.util.Optional;

import study.snacktrack.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Admin entities.
 * This extends JpaRepository to provide standard CRUD operations and custom query methods for administrator accounts.
 */
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    /**
     * Retrieves an administrative user by their email address.
     * This method returns an Optional wrapper to safely handle cases where no admin is found with the given email.
     *
     * @param email the email address of the admin
     * @return an Optional containing the Admin entity or empty if not found
     */
    Optional<Admin> findByEmail(String email);
}