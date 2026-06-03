package io.devflow.issues.entity;

import io.devflow.common.entity.UuidEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
        name = "issue_assignees",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_issue_assignees_issue_user",
                columnNames = {"issue_id", "user_id"}
        ),
        indexes = @Index(name = "idx_issue_assignees_user", columnList = "user_id")
)
public class IssueAssignee extends UuidEntity {

    @Column(name = "issue_id", nullable = false)
    private UUID issueId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "assigned_by_id")
    private UUID assignedById;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private Instant assignedAt;

    @PrePersist
    protected void initializeAssignedAt() {
        if (assignedAt == null) {
            assignedAt = Instant.now();
        }
    }
}
