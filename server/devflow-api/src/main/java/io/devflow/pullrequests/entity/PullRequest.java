package io.devflow.pullrequests.entity;

import io.devflow.common.entity.BaseEntity;
import io.devflow.pullrequests.enums.PullRequestStatus;
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
        name = "pull_requests",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_pull_requests_repository_number",
                columnNames = {"repository_id", "pull_request_number"}
        ),
        indexes = {
                @Index(name = "idx_pull_requests_repository_status", columnList = "repository_id, status"),
                @Index(name = "idx_pull_requests_author", columnList = "author_id"),
                @Index(name = "idx_pull_requests_milestone", columnList = "milestone_id")
        }
)
public class PullRequest extends BaseEntity {

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "pull_request_number", nullable = false)
    private int pullRequestNumber;

    @Column(name = "author_id")
    private UUID authorId;

    @Column(name = "milestone_id")
    private UUID milestoneId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PullRequestStatus status = PullRequestStatus.OPEN;

    @Column(name = "source_branch_id", nullable = false)
    private UUID sourceBranchId;

    @Column(name = "target_branch_id", nullable = false)
    private UUID targetBranchId;

    @Column(name = "merge_commit_id")
    private UUID mergeCommitId;

    @Column(name = "merged_by_id")
    private UUID mergedById;

    @Column(name = "merged_at")
    private Instant mergedAt;

    @Column(name = "closed_by_id")
    private UUID closedById;

    @Column(name = "closed_at")
    private Instant closedAt;

    public void merge(UUID mergedById, UUID mergeCommitId) {
        status = PullRequestStatus.MERGED;
        this.mergedById = mergedById;
        this.mergeCommitId = mergeCommitId;
        mergedAt = Instant.now();
    }

    public void close(UUID closedById) {
        status = PullRequestStatus.CLOSED;
        this.closedById = closedById;
        closedAt = Instant.now();
    }
}
