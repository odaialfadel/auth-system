package com.odai.auth.shared.dto.registeration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegistrationRequest(

    @Email
    @NotBlank
    String email,

    @NotBlank
    String firstName,

    @NotBlank
    String lastName
) {}
