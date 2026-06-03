package io.devflow.issues.entity;

import io.devflow.common.entity.BaseEntity;
import io.devflow.issues.enums.IssueStatus;
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
        name = "issues",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_issues_repository_number",
                columnNames = {"repository_id", "issue_number"}
        ),
        indexes = {
                @Index(name = "idx_issues_repository_status", columnList = "repository_id, status"),
                @Index(name = "idx_issues_author", columnList = "author_id"),
                @Index(name = "idx_issues_milestone", columnList = "milestone_id")
        }
)
public class Issue extends BaseEntity {

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "issue_number", nullable = false)
    private int issueNumber;

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
    private IssueStatus status = IssueStatus.OPEN;

    @Column(name = "state_reason", length = 50)
    private String stateReason;

    @Column(name = "closed_by_id")
    private UUID closedById;

    @Column(name = "closed_at")
    private Instant closedAt;

    public void close(UUID closedById, String reason) {
        status = IssueStatus.CLOSED;
        this.closedById = closedById;
        stateReason = reason;
        closedAt = Instant.now();
    }

    public void reopen() {
        status = IssueStatus.OPEN;
        closedById = null;
        stateReason = null;
        closedAt = null;
    }
}
