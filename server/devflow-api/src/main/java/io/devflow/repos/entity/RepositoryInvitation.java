package io.devflow.repos.entity;

import io.devflow.common.entity.CreatedEntity;
import io.devflow.repos.enums.RepositoryMemberRole;
import io.devflow.users.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "repository_invitations")
public class RepositoryInvitation extends CreatedEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_user_id")
    private User invitedUser;

    @Column(name = "invited_email", length = 255)
    private String invitedEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private RepositoryMemberRole role;

    @Column(name = "token_hash", nullable = false, length = 255)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by_id")
    private User invitedBy;

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
