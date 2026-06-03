package io.devflow.pullrequests.entity;

import io.devflow.common.entity.CreatedEntity;
import io.devflow.pullrequests.enums.PullRequestReviewerType;
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
        name = "pull_request_review_requests",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_pull_request_review_requests_reviewer",
                columnNames = {"pull_request_id", "reviewer_type", "reviewer_id"}
        ),
        indexes = @Index(name = "idx_pull_request_review_requests_reviewer", columnList = "reviewer_type, reviewer_id")
)
public class PullRequestReviewRequest extends CreatedEntity {

    @Column(name = "pull_request_id", nullable = false)
    private UUID pullRequestId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reviewer_type", nullable = false, length = 20)
    private PullRequestReviewerType reviewerType = PullRequestReviewerType.USER;

    @Column(name = "reviewer_id", nullable = false)
    private UUID reviewerId;

    @Column(name = "requested_by_id")
    private UUID requestedById;

    @Column(name = "fulfilled_at")
    private Instant fulfilledAt;

    @Column(name = "removed_at")
    private Instant removedAt;

    public boolean isPending() {
        return fulfilledAt == null && removedAt == null;
    }

    public void fulfill() {
        fulfilledAt = Instant.now();
    }

    public void remove() {
        removedAt = Instant.now();
    }
}
