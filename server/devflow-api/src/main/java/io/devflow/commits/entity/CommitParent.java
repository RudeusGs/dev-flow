package io.devflow.commits.entity;

import io.devflow.common.entity.UuidEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "commit_parents",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_commit_parents_commit_parent",
                        columnNames = {"commit_id", "parent_commit_id"}
                ),
                @UniqueConstraint(
                        name = "uk_commit_parents_commit_order",
                        columnNames = {"commit_id", "parent_order"}
                )
        },
        indexes = {
                @Index(name = "idx_commit_parents_repository", columnList = "repository_id"),
                @Index(name = "idx_commit_parents_parent", columnList = "parent_commit_id")
        }
)
public class CommitParent extends UuidEntity {

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "commit_id", nullable = false)
    private UUID commitId;

    @Column(name = "parent_commit_id", nullable = false)
    private UUID parentCommitId;

    @Column(name = "parent_order", nullable = false)
    private int parentOrder;
}
