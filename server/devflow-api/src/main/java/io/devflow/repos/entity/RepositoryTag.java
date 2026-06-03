package io.devflow.repos.entity;

import io.devflow.common.entity.CreatedEntity;
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
        name = "repository_tags",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_repository_tags_repository_name",
                columnNames = {"repository_id", "name"}
        ),
        indexes = @Index(name = "idx_repository_tags_commit", columnList = "commit_id")
)
public class RepositoryTag extends CreatedEntity {

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "commit_id", nullable = false)
    private UUID commitId;

    @Column(name = "tagged_by_id")
    private UUID taggedById;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
}
