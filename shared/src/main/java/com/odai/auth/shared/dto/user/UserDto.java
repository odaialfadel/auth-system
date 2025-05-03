package com.odai.auth.shared.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Email
    private String email;

    @NotBlank
    @Size(min = 5, max = 50)
    private String username;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private boolean isActive;

    List<String> roles;
}
