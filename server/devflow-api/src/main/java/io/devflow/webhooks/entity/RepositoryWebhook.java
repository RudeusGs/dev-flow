package io.devflow.webhooks.entity;

import io.devflow.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "repository_webhooks",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_repository_webhooks_repository_url",
                columnNames = {"repository_id", "url"}
        ),
        indexes = @Index(name = "idx_repository_webhooks_repository", columnList = "repository_id")
)
public class RepositoryWebhook extends BaseEntity {

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "created_by_id")
    private UUID createdById;

    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "secret_hash", length = 255)
    private String secretHash;

    @Column(name = "content_type", nullable = false, length = 40)
    private String contentType = "json";

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "events", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> events = new HashMap<>();

    @Column(name = "last_delivered_at")
    private Instant lastDeliveredAt;

    public void markDelivered() {
        lastDeliveredAt = Instant.now();
    }

    public void disable() {
        active = false;
    }

    public void enable() {
        active = true;
    }
}
