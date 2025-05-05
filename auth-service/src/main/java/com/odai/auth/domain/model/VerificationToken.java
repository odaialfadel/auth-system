package com.odai.auth.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "email_verification_token")
public class VerificationToken {

    @Id
    private String token;

    private String userId;

    private ZonedDateTime expiredAt;

    public boolean isExpired() {
        return expiredAt.isBefore(ZonedDateTime.now());
    }
}
