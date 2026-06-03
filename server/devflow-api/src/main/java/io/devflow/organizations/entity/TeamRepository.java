package io.devflow.organizations.entity;

import io.devflow.common.entity.CreatedEntity;
import io.devflow.repos.enums.RepositoryMemberRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
        name = "team_repositories",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_team_repositories_team_repository",
                columnNames = {"team_id", "repository_id"}
        ),
        indexes = @Index(name = "idx_team_repositories_repository", columnList = "repository_id")
)
public class TeamRepository extends CreatedEntity {

    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private RepositoryMemberRole role = RepositoryMemberRole.VIEWER;

    @Column(name = "added_by_id")
    private UUID addedById;
}
