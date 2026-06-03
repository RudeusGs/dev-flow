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
        name = "repository_stars",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_repository_stars_repository_user",
                columnNames = {"repository_id", "user_id"}
        ),
        indexes = @Index(name = "idx_repository_stars_user", columnList = "user_id")
)
public class RepositoryStar extends CreatedEntity {

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;
}
