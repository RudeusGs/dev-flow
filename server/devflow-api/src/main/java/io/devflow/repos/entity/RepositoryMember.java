package io.devflow.repos.entity;

import io.devflow.common.entity.UuidEntity;
import io.devflow.repos.enums.RepositoryMemberRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
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
        name = "repository_members",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_repository_members_repository_user",
                columnNames = {"repository_id", "user_id"}
        ),
        indexes = @Index(name = "idx_repository_members_user", columnList = "user_id")
)
public class RepositoryMember extends UuidEntity {

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private RepositoryMemberRole role;

    @Column(name = "invited_by_id")
    private UUID invitedById;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    @Column(name = "removed_at")
    private Instant removedAt;

    @PrePersist
    protected void initializeJoinedAt() {
        if (joinedAt == null) {
            joinedAt = Instant.now();
        }
    }

    public boolean isActive() {
        return removedAt == null;
    }

    public void remove() {
        removedAt = Instant.now();
    }
}
