package io.devflow.issues.entity;

import io.devflow.common.entity.BaseEntity;
import io.devflow.issues.enums.MilestoneStatus;
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
        name = "milestones",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_milestones_repository_title",
                columnNames = {"repository_id", "title"}
        ),
        indexes = @Index(name = "idx_milestones_repository_status", columnList = "repository_id, status")
)
public class Milestone extends BaseEntity {

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "created_by_id")
    private UUID createdById;

    @Column(name = "title", nullable = false, length = 160)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MilestoneStatus status = MilestoneStatus.OPEN;

    @Column(name = "due_at")
    private Instant dueAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    public void close() {
        status = MilestoneStatus.CLOSED;
        closedAt = Instant.now();
    }

    public void reopen() {
        status = MilestoneStatus.OPEN;
        closedAt = null;
    }
}
