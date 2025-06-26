package com.odai.auth.service.auth;

import com.odai.auth.domain.model.VerificationToken;
import com.odai.auth.domain.repository.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VerificationTokenServiceTest {

    private VerificationTokenRepository tokenRepository;
    private VerificationTokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenRepository = mock(VerificationTokenRepository.class);
        tokenService = new VerificationTokenService(tokenRepository);
    }

    @Test
    void testCreateToken() {
        long userId = 123L;

        ArgumentCaptor<VerificationToken> captor = ArgumentCaptor.forClass(VerificationToken.class);
        when(tokenRepository.save(any(VerificationToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        VerificationToken createdToken = tokenService.createToken(userId);

        assertNotNull(createdToken.getToken());
        assertEquals(userId, createdToken.getUserId());
        assertTrue(createdToken.getExpiredAt().isAfter(ZonedDateTime.now()));

        verify(tokenRepository).save(captor.capture());
        VerificationToken saved = captor.getValue();
        assertEquals(createdToken.getToken(), saved.getToken());
    }

    @Test
    void testFindValidToken_whenNotExpired() {
        String createdToken = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(createdToken, 123L, ZonedDateTime.now().plusHours(1));

        when(tokenRepository.findById(createdToken)).thenReturn(Optional.of(verificationToken));

        Optional<VerificationToken> result = tokenService.findValidToken(createdToken);

        assertTrue(result.isPresent());
        assertEquals(createdToken, result.get().getToken());
    }

    @Test
    void testFindValidToken_whenExpired() {
        String createdToken = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(createdToken, 123L, ZonedDateTime.now().minusHours(1));

        when(tokenRepository.findById(createdToken)).thenReturn(Optional.of(verificationToken));

        Optional<VerificationToken> result = tokenService.findValidToken(createdToken);

        assertTrue(result.isEmpty());
    }

    @Test
    void testConsumeToken() {
        String createdToken = UUID.randomUUID().toString();

        tokenService.consumeToken(createdToken);

        verify(tokenRepository).deleteById(createdToken);
    }
}