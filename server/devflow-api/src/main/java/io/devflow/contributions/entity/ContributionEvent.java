package io.devflow.contributions.entity;

import io.devflow.common.entity.CreatedEntity;
import io.devflow.contributions.enums.ContributionType;
import io.devflow.contributions.enums.ContributionVisibility;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "contribution_events",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_contribution_events_source",
                columnNames = {"user_id", "contribution_type", "source_type", "source_id"}
        ),
        indexes = {
                @Index(name = "idx_contribution_events_user_date", columnList = "user_id, contribution_date"),
                @Index(name = "idx_contribution_events_repository_date", columnList = "repository_id, contribution_date"),
                @Index(name = "idx_contribution_events_type_date", columnList = "contribution_type, contribution_date")
        }
)
public class ContributionEvent extends CreatedEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "repository_id")
    private UUID repositoryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "contribution_type", nullable = false, length = 60)
    private ContributionType contributionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 20)
    private ContributionVisibility visibility = ContributionVisibility.PUBLIC;

    @Column(name = "contribution_date", nullable = false)
    private LocalDate contributionDate;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "source_type", nullable = false, length = 80)
    private String sourceType;

    @Column(name = "source_id", nullable = false)
    private UUID sourceId;

    @Column(name = "weight", nullable = false)
    private int weight = 1;

    @Column(name = "is_ignored", nullable = false)
    private boolean ignored;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> metadata = new HashMap<>();

    public boolean isPublicContribution() {
        return visibility == ContributionVisibility.PUBLIC;
    }
}
