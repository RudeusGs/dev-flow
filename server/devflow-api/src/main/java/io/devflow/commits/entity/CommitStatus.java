package io.devflow.commits.entity;

import io.devflow.commits.enums.CommitStatusState;
import io.devflow.common.entity.CreatedEntity;
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
        name = "commit_statuses",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_commit_statuses_repository_commit_context",
                columnNames = {"repository_id", "commit_id", "context"}
        ),
        indexes = @Index(name = "idx_commit_statuses_commit_state", columnList = "commit_id, state")
)
public class CommitStatus extends CreatedEntity {

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "commit_id", nullable = false)
    private UUID commitId;

    @Column(name = "creator_id")
    private UUID creatorId;

    @Column(name = "context", nullable = false, length = 160)
    private String context;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    private CommitStatusState state = CommitStatusState.PENDING;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "target_url", columnDefinition = "TEXT")
    private String targetUrl;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    public void markStarted() {
        state = CommitStatusState.PENDING;
        startedAt = Instant.now();
        completedAt = null;
    }

    public void complete(CommitStatusState state) {
        this.state = state;
        completedAt = Instant.now();
    }
}
