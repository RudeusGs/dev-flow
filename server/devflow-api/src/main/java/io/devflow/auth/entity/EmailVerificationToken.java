package io.devflow.auth.entity;

import io.devflow.common.entity.CreatedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "email_verification_tokens",
        uniqueConstraints = @UniqueConstraint(name = "uk_email_verification_tokens_token_hash", columnNames = "token_hash"),
        indexes = {
                @Index(name = "idx_email_verification_tokens_user", columnList = "user_id"),
                @Index(name = "idx_email_verification_tokens_expires_at", columnList = "expires_at")
        }
)
public class EmailVerificationToken extends CreatedEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "token_hash", nullable = false, length = 255)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    public boolean isAvailable() {
        return verifiedAt == null && expiresAt.isAfter(Instant.now());
    }

    public void markVerified() {
        verifiedAt = Instant.now();
    }
}
