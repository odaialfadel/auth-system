package com.odai.auth.shared.dto.registeration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest (

        @NotBlank(message = "Username is required")
        @Size(min = 5, max = 30, message = "Username must be between 5 and 30 characters")
        @Pattern(
                regexp = "^[a-zA-Z][a-zA-Z0-9_.-]{4,29}$",
                message = "Username must start with a letter and can contain letters, numbers, underscores, dots, and dashes (5–30 characters)"
        )
        String username,

        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 30, message = "First name must be between 2 and 30 characters")
        @Pattern(
                regexp = "^[A-Za-z][A-Za-z_\\-\\s]{1,29}$",
                message = "First name must start with a letter and contain only letters, underscores (_), dashes (-), or spaces"
        )
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 30, message = "Last name must be between 2 and 30 characters")
        @Pattern(
                regexp = "^[A-Za-z][A-Za-z_\\-\\s]{1,29}$",
                message = "Last name must start with a letter and contain only letters, underscores (_), dashes (-), or spaces"
        )
        String lastName,

        @Email(message = "Please enter a valid email address")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
        )
        String password
) {}
