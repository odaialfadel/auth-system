package com.odai.auth.shared.dto.login;

public record LoginResponse(
        String accessToken,
        long expiresIn,
        String tokenType
) {}
