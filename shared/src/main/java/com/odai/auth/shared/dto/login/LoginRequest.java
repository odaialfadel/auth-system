package com.odai.auth.shared.dto.login;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String emailOrUsername,
        @NotBlank String password
) {}
