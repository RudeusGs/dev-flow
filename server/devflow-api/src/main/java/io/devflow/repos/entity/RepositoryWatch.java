package io.devflow.repos.entity;

import io.devflow.common.entity.CreatedEntity;
import io.devflow.repos.enums.WatchNotificationLevel;
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
        name = "repository_watches",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_repository_watches_repository_user",
                columnNames = {"repository_id", "user_id"}
        ),
        indexes = @Index(name = "idx_repository_watches_user", columnList = "user_id")
)
public class RepositoryWatch extends CreatedEntity {

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_level", nullable = false, length = 30)
    private WatchNotificationLevel notificationLevel = WatchNotificationLevel.PARTICIPATING;
}
