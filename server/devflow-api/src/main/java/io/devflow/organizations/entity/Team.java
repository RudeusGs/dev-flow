package io.devflow.organizations.entity;

import io.devflow.common.entity.BaseEntity;
import io.devflow.organizations.enums.TeamPrivacy;
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
        name = "teams",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_teams_organization_slug",
                columnNames = {"organization_id", "slug"}
        ),
        indexes = @Index(name = "idx_teams_organization", columnList = "organization_id")
)
public class Team extends BaseEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "parent_team_id")
    private UUID parentTeamId;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "slug", nullable = false, length = 140)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "privacy", nullable = false, length = 30)
    private TeamPrivacy privacy = TeamPrivacy.VISIBLE;
}
