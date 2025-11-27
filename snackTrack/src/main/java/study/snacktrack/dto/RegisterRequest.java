package study.snacktrack.dto;

import study.snacktrack.entities.enums.Status;

/**
 * Data Transfer Object (DTO) used for carrying all necessary information when a new user registers in the application.
 * This structure encapsulates the user's basic identifying information and desired initial account status.
 */
public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    private String surname;
    private Status status;

    /**
     * Getters and setters for the DTO fields.
     * These methods provide standard access and modification capabilities for the user's registration details.
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}