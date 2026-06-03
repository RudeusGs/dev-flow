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
        name = "repository_forks",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_repository_forks_source_fork",
                columnNames = {"source_repository_id", "fork_repository_id"}
        ),
        indexes = @Index(name = "idx_repository_forks_forked_by", columnList = "forked_by_id")
)
public class RepositoryFork extends CreatedEntity {

    @Column(name = "source_repository_id", nullable = false)
    private UUID sourceRepositoryId;

    @Column(name = "fork_repository_id", nullable = false)
    private UUID forkRepositoryId;

    @Column(name = "forked_by_id", nullable = false)
    private UUID forkedById;
}
