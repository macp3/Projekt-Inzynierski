package study.snacktrack.dto;

/**
 * Data Transfer Object (DTO) used for carrying user credentials during the authentication process.
 * This structure securely encapsulates the email and password required for a user to log into the application.
 */
public class LoginRequest {

    private String email;
    private String password;

    /**
     * Getters and setters for the DTO fields.
     * These methods provide standard access and modification capabilities for the user's login credentials.
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}