package study.snacktrack.entities;

import jakarta.persistence.*;

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

    public UserDeviceToken(Integer userId, String deviceToken) {
        this.userId = userId;
        this.deviceToken = deviceToken;
    }

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
