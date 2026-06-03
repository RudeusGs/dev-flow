package io.devflow.branches.entity;

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
        name = "branch_commits",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_branch_commits_branch_commit",
                columnNames = {"branch_id", "commit_id"}
        ),
        indexes = {
                @Index(name = "idx_branch_commits_repository", columnList = "repository_id"),
                @Index(name = "idx_branch_commits_branch_committed_at", columnList = "branch_id, committed_at"),
                @Index(name = "idx_branch_commits_commit", columnList = "commit_id")
        }
)
public class BranchCommit extends CreatedEntity {

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "branch_id", nullable = false)
    private UUID branchId;

    @Column(name = "commit_id", nullable = false)
    private UUID commitId;

    @Column(name = "committed_at", nullable = false)
    private Instant committedAt;
}
