package io.devflow.commits.entity;

import io.devflow.common.entity.CreatedEntity;
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
        name = "commits",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_commits_repository_hash",
                columnNames = {"repository_id", "commit_hash"}
        ),
        indexes = {
                @Index(name = "idx_commits_branch", columnList = "branch_id"),
                @Index(name = "idx_commits_parent", columnList = "parent_commit_id")
        }
)
public class Commit extends CreatedEntity {

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "branch_id", nullable = false)
    private UUID branchId;

    @Column(name = "author_id")
    private UUID authorId;

    @Column(name = "parent_commit_id")
    private UUID parentCommitId;

    @Column(name = "commit_hash", nullable = false, length = 80)
    private String commitHash;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "committed_at", nullable = false)
    private Instant committedAt;

    @PrePersist
    protected void initializeCommittedAt() {
        if (committedAt == null) {
            committedAt = Instant.now();
        }
    }
}
