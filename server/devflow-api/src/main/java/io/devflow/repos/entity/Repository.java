package io.devflow.repos.entity;

import io.devflow.common.entity.BaseEntity;
import io.devflow.repos.enums.RepositoryOwnerType;
import io.devflow.repos.enums.RepositoryStatus;
import io.devflow.repos.enums.RepositoryVisibility;
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
        name = "repositories",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_repositories_owner_slug",
                columnNames = {"owner_type", "owner_id", "slug"}
        ),
        indexes = {
                @Index(name = "idx_repositories_owner", columnList = "owner_type, owner_id"),
                @Index(name = "idx_repositories_status", columnList = "status")
        }
)
public class Repository extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false, length = 30)
    private RepositoryOwnerType ownerType = RepositoryOwnerType.USER;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "slug", nullable = false, length = 140)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 20)
    private RepositoryVisibility visibility = RepositoryVisibility.PUBLIC;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RepositoryStatus status = RepositoryStatus.ACTIVE;

    @Column(name = "default_branch_name", nullable = false, length = 120)
    private String defaultBranchName = "main";

    @Column(name = "readme_file_id")
    private UUID readmeFileId;

    @Column(name = "forked_from_repository_id")
    private UUID forkedFromRepositoryId;

    @Column(name = "is_template", nullable = false)
    private boolean template;

    @Column(name = "stars_count", nullable = false)
    private int starsCount;

    @Column(name = "forks_count", nullable = false)
    private int forksCount;

    @Column(name = "issues_count", nullable = false)
    private int issuesCount;

    @Column(name = "pull_requests_count", nullable = false)
    private int pullRequestsCount;

    @Column(name = "next_issue_number", nullable = false)
    private int nextIssueNumber = 1;

    @Column(name = "next_pull_request_number", nullable = false)
    private int nextPullRequestNumber = 1;

    @Column(name = "archived_at")
    private Instant archivedAt;

    public void archive() {
        status = RepositoryStatus.ARCHIVED;
        archivedAt = Instant.now();
    }

    @Override
    public void softDelete() {
        super.softDelete();
        status = RepositoryStatus.DELETED;
    }

    @Override
    public void restore() {
        super.restore();
        status = RepositoryStatus.ACTIVE;
        archivedAt = null;
    }
}
