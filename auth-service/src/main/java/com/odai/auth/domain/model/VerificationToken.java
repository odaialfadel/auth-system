package com.odai.auth.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "email_verification_token")
public class VerificationToken {

    @Id
    private String token;

    private Long userId;

    private ZonedDateTime expiredAt;

    public boolean isExpired() {
        return expiredAt.isBefore(ZonedDateTime.now());
    }
}
