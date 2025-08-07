package com.projectmanagement.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * DTO for user update requests
 */
public class UserUpdateRequest {

    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Size(max = 50, message = "Role must not exceed 50 characters")
    private String role;

    private Boolean isActive;

    // Constructors
    public UserUpdateRequest() {
    }

    public UserUpdateRequest(String firstName, String lastName, String email, String role, Boolean isActive) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "UserUpdateRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", isActive=" + isActive +
                '}';
    }
} 