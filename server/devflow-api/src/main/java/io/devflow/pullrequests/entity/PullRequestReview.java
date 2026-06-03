package io.devflow.pullrequests.entity;

import io.devflow.common.entity.UuidEntity;
import io.devflow.pullrequests.enums.PullRequestReviewStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "pull_request_reviews",
        indexes = {
                @Index(name = "idx_pull_request_reviews_pull_request", columnList = "pull_request_id"),
                @Index(name = "idx_pull_request_reviews_reviewer", columnList = "reviewer_id")
        }
)
public class PullRequestReview extends UuidEntity {

    @Column(name = "pull_request_id", nullable = false)
    private UUID pullRequestId;

    @Column(name = "reviewer_id", nullable = false)
    private UUID reviewerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PullRequestReviewStatus status;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private Instant submittedAt;

    @Column(name = "dismissed_by_id")
    private UUID dismissedById;

    @Column(name = "dismissed_at")
    private Instant dismissedAt;

    @Column(name = "dismiss_reason", columnDefinition = "TEXT")
    private String dismissReason;

    @PrePersist
    protected void initializeSubmittedAt() {
        if (submittedAt == null) {
            submittedAt = Instant.now();
        }
    }

    public void dismiss(UUID dismissedById, String reason) {
        this.dismissedById = dismissedById;
        dismissReason = reason;
        dismissedAt = Instant.now();
    }
}
