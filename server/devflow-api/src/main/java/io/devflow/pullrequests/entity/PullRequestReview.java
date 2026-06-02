package io.devflow.pullrequests.entity;

import io.devflow.common.entity.UuidEntity;
import io.devflow.pullrequests.enums.PullRequestReviewStatus;
import io.devflow.users.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "pull_request_reviews")
public class PullRequestReview extends UuidEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pull_request_id", nullable = false)
    private PullRequest pullRequest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PullRequestReviewStatus status;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private Instant submittedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dismissed_by_id")
    private User dismissedBy;

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

    public void dismiss(User user, String reason) {
        dismissedBy = user;
        dismissReason = reason;
        dismissedAt = Instant.now();
    }
}
