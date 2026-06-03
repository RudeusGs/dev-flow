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
                @Index(name = "idx_commits_repository_committed_at", columnList = "repository_id, committed_at"),
                @Index(name = "idx_commits_author", columnList = "author_id")
        }
)
public class Commit extends CreatedEntity {

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "author_id")
    private UUID authorId;

    @Column(name = "commit_hash", nullable = false, length = 80)
    private String commitHash;

    @Column(name = "tree_hash", length = 80)
    private String treeHash;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "author_name", length = 160)
    private String authorName;

    @Column(name = "author_email", length = 255)
    private String authorEmail;

    @Column(name = "committer_name", length = 160)
    private String committerName;

    @Column(name = "committer_email", length = 255)
    private String committerEmail;

    @Column(name = "authored_at")
    private Instant authoredAt;

    @Column(name = "committed_at", nullable = false)
    private Instant committedAt;

    @PrePersist
    protected void initializeCommittedAt() {
        if (committedAt == null) {
            committedAt = Instant.now();
        }
        if (authoredAt == null) {
            authoredAt = committedAt;
        }
    }
}
