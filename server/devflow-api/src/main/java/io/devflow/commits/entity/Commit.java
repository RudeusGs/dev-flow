package io.devflow.commits.entity;

import io.devflow.branches.entity.Branch;
import io.devflow.common.entity.CreatedEntity;
import io.devflow.repos.entity.Repository;
import io.devflow.users.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "commits")
public class Commit extends CreatedEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_commit_id")
    private Commit parentCommit;

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
