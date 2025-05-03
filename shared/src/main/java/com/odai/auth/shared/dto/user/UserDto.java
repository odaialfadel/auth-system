package com.odai.auth.shared.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    private UUID keycloakId;

    @NotBlank
    @Pattern(
            regexp = "^[a-zA-Z][a-zA-Z0-9_.-]{4,29}$",
            message = "Username must start with a letter and can contain letters, numbers, underscores, dots, and dashes (5–30 characters)"
    )
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z_\\-\\s]{1,29}$",
            message = "First name must start with a letter and contain only letters, underscores (_), dashes (-), or spaces"
    )
    private String firstName;

    @NotBlank
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z_\\-\\s]{1,29}$",
            message = "Last name must start with a letter and contain only letters, underscores (_), dashes (-), or spaces"
    )
    private String lastName;

    private boolean isActive;

    List<String> roles;
}
