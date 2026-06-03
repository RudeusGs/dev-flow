package io.devflow.organizations.entity;

import io.devflow.common.entity.UuidEntity;
import io.devflow.organizations.enums.OrganizationMemberRole;
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
        name = "organization_members",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_organization_members_organization_user",
                columnNames = {"organization_id", "user_id"}
        ),
        indexes = @Index(name = "idx_organization_members_user", columnList = "user_id")
)
public class OrganizationMember extends UuidEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private OrganizationMemberRole role = OrganizationMemberRole.MEMBER;

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
