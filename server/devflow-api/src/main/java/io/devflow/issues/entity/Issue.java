package io.devflow.issues.entity;

import io.devflow.common.entity.BaseEntity;
import io.devflow.issues.enums.IssueStatus;
import io.devflow.repos.entity.Repository;
import io.devflow.users.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "issues")
public class Issue extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;

    @Column(name = "issue_number", nullable = false)
    private int issueNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private IssueStatus status = IssueStatus.OPEN;

    @Column(name = "state_reason", length = 50)
    private String stateReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closed_by_id")
    private User closedBy;

    @Column(name = "closed_at")
    private Instant closedAt;

    public void close(User user, String reason) {
        status = IssueStatus.CLOSED;
        closedBy = user;
        stateReason = reason;
        closedAt = Instant.now();
    }

    public void reopen() {
        status = IssueStatus.OPEN;
        closedBy = null;
        stateReason = null;
        closedAt = null;
    }
}
