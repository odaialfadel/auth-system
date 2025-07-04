package com.odai.auth.service.auth;

import com.odai.auth.domain.model.VerificationToken;
import com.odai.auth.domain.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing email verification tokens.
 * <p>
 * Responsible for creating new tokens, retrieving valid (non-expired) tokens,
 * and consuming (deleting) tokens after use.
 * </p>
 */
@AllArgsConstructor
@Service
public class VerificationTokenService {
    private final VerificationTokenRepository tokenRepository;

    /**
     * Creates a new verification token for the specified user.
     * <p>
     * The token is a random UUID and expires 24 hours after creation.
     * </p>
     *
     * @param userId the ID of the user for whom the token is generated
     * @return the saved {@link VerificationToken} entity
     */
    public VerificationToken createToken(long userId) {
        String token = UUID.randomUUID().toString();
        ZonedDateTime expiresAt = ZonedDateTime.now().plusHours(24);
        VerificationToken entity = new VerificationToken(token, userId, expiresAt);
        return tokenRepository.save(entity);
    }

    /**
     * Finds a valid (non-expired) verification token by its token string.
     *
     * @param token the token string to look up
     * @return an {@link Optional} containing the valid token if found and not expired, otherwise empty
     */
    public Optional<VerificationToken> findValidToken(String token) {
        return tokenRepository.findById(token)
                .filter(t -> !t.isExpired());
    }

    /**
     * Consumes a token by deleting it from the repository.
     * <p>
     * This should be called after a token has been used (e.g., after email verification).
     * </p>
     *
     * @param token the token string to consume/delete
     */
    public void consumeToken(String token) {
        tokenRepository.deleteById(token);
    }
}
