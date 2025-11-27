package study.snacktrack.entities;

import jakarta.persistence.*;

/**
 * Represents a unique registration token used to send push notifications to a
 * specific user's device.
 * This entity links a user's ID to the unique device identifier needed for
 * messaging services like Firebase Cloud Messaging.
 */
@Entity
@Table(name = "user_device_tokens")
public class UserDeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "device_token", nullable = false)
    private String deviceToken;

    public UserDeviceToken() {
    }

    /**
     * Constructs a new UserDeviceToken entity, associating a specific user with
     * their unique device identifier.
     * This constructor is used when a user logs into a new device and registers for
     * push notifications.
     *
     * @param userId      The ID of the user associated with the device.
     * @param deviceToken The unique token identifying the user's device for push
     *                    notifications.
     */
    public UserDeviceToken(Integer userId, String deviceToken) {
        this.userId = userId;
        this.deviceToken = deviceToken;
    }

    /**
     * Getters and setters for all entity fields.
     * These methods provide standard access and modification capabilities for the
     * UserDeviceToken entity properties.
     */
    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}