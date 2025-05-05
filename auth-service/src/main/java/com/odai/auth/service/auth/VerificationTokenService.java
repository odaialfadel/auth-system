package com.odai.auth.service.auth;

import com.odai.auth.domain.model.VerificationToken;
import com.odai.auth.domain.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class VerificationTokenService {
    private final VerificationTokenRepository tokenRepository;

    public VerificationToken createToken(String userId) {
        String token = UUID.randomUUID().toString();
        ZonedDateTime expiresAt = ZonedDateTime.now().plusHours(24);
        VerificationToken entity = new VerificationToken(token, userId, expiresAt);
        return tokenRepository.save(entity);
    }

    public Optional<VerificationToken> findValidToken(String token) {
        return tokenRepository.findById(token)
                .filter(t -> !t.isExpired());
    }

    public void consumeToken(String token) {
        tokenRepository.deleteById(token);
    }
}
