package com.odai.auth.shared.dto.email;

public record EmailVerificationRequest(
        String userId,
        String userEmail,
        String firstName
) {}
