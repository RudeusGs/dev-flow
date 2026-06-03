package io.devflow.repos.entity;

import io.devflow.common.entity.BaseEntity;
import io.devflow.repos.enums.ReleaseStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "repository_releases",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_repository_releases_repository_tag",
                columnNames = {"repository_id", "tag_name"}
        ),
        indexes = @Index(name = "idx_repository_releases_repository_status", columnList = "repository_id, status")
)
public class RepositoryRelease extends BaseEntity {

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "tag_id")
    private UUID tagId;

    @Column(name = "tag_name", nullable = false, length = 120)
    private String tagName;

    @Column(name = "author_id")
    private UUID authorId;

    @Column(name = "name", nullable = false, length = 160)
    private String name;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReleaseStatus status = ReleaseStatus.DRAFT;

    @Column(name = "is_prerelease", nullable = false)
    private boolean prerelease;

    @Column(name = "published_at")
    private Instant publishedAt;

    public void publish() {
        status = ReleaseStatus.PUBLISHED;
        publishedAt = Instant.now();
    }

    public void unpublish() {
        status = ReleaseStatus.DRAFT;
        publishedAt = null;
    }
}
