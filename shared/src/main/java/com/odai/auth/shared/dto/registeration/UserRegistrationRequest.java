package com.odai.auth.shared.dto.registeration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserRegistrationRequest (

        @NotBlank
        @Pattern(
                regexp = "^[a-zA-Z][a-zA-Z0-9_.-]{4,29}$",
                message = "Username must start with a letter and can contain letters, numbers, underscores, dots, and dashes (5–30 characters)"
        )
        String username,

        @Email
        @NotBlank
        String email,

        @NotBlank
        @Pattern(
                regexp = "^[A-Za-z][A-Za-z_\\-\\s]{1,29}$",
                message = "First name must start with a letter and contain only letters, underscores (_), dashes (-), or spaces"
        )
        String firstName,

        @NotBlank
        @Pattern(
                regexp = "^[A-Za-z][A-Za-z_\\-\\s]{1,29}$",
                message = "Last name must start with a letter and contain only letters, underscores (_), dashes (-), or spaces"
        )
        String lastName
) {}
