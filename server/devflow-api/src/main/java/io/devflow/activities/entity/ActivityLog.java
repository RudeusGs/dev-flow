package io.devflow.activities.entity;

import io.devflow.common.entity.CreatedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "activity_logs",
        indexes = {
                @Index(name = "idx_activity_logs_repository", columnList = "repository_id"),
                @Index(name = "idx_activity_logs_entity", columnList = "entity_type, entity_id")
        }
)
public class ActivityLog extends CreatedEntity {

    @Column(name = "actor_id")
    private UUID actorId;

    @Column(name = "repository_id")
    private UUID repositoryId;

    @Column(name = "entity_type", nullable = false, length = 80)
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(name = "action", nullable = false, length = 80)
    private String action;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> metadata = new HashMap<>();
}
