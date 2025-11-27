package study.snacktrack.repositories;

import java.util.List;

import study.snacktrack.entities.UserDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing UserDeviceToken entities, which store push notification tokens for users.
 * This extends JpaRepository to provide standard persistence operations and specialized methods for token management.
 */
public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Integer> {

    /**
     * Retrieves all device tokens associated with a specific user ID.
     * This is crucial for sending push notifications to all devices registered by a single user.
     *
     * @param userId the ID of the user
     * @return a List of UserDeviceToken entities belonging to the specified user
     */
    List<UserDeviceToken> findByUserId(Integer userId);

    /**
     * Deletes a record based on the unique push notification device token string.
     * This method is typically used to remove expired or invalidated tokens to maintain a clean database.
     *
     * @param deviceToken the unique token string to delete
     */
    void deleteByDeviceToken(String deviceToken);
}