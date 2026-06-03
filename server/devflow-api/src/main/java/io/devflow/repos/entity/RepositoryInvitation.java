package io.devflow.repos.entity;

import io.devflow.common.entity.CreatedEntity;
import io.devflow.repos.enums.RepositoryMemberRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
        name = "repository_invitations",
        uniqueConstraints = @UniqueConstraint(name = "uk_repository_invitations_token_hash", columnNames = "token_hash"),
        indexes = {
                @Index(name = "idx_repository_invitations_repository", columnList = "repository_id"),
                @Index(name = "idx_repository_invitations_invited_user", columnList = "invited_user_id"),
                @Index(name = "idx_repository_invitations_invited_email", columnList = "invited_email")
        }
)
public class RepositoryInvitation extends CreatedEntity {

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "invited_user_id")
    private UUID invitedUserId;

    @Column(name = "invited_email", length = 255)
    private String invitedEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private RepositoryMemberRole role;

    @Column(name = "token_hash", nullable = false, length = 255)
    private String tokenHash;

    @Column(name = "invited_by_id")
    private UUID invitedById;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "accepted_at")
    private Instant acceptedAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    public boolean isAvailable() {
        return acceptedAt == null && revokedAt == null && expiresAt.isAfter(Instant.now());
    }

    public void accept() {
        acceptedAt = Instant.now();
    }

    public void revoke() {
        revokedAt = Instant.now();
    }
}
