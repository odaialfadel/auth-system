package com.odai.auth.shared.dto.registeration;

import java.util.UUID;

public record UserRegistrationResponse (
        Long userId,
        UUID keycloakId,
        String username,
        String email,
        String message
) {}
