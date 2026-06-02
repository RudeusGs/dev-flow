package io.devflow.pullrequests.entity;

import io.devflow.branches.entity.Branch;
import io.devflow.commits.entity.Commit;
import io.devflow.common.entity.BaseEntity;
import io.devflow.pullrequests.enums.PullRequestStatus;
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
@Table(name = "pull_requests")
public class PullRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;

    @Column(name = "pull_request_number", nullable = false)
    private int pullRequestNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PullRequestStatus status = PullRequestStatus.OPEN;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_branch_id", nullable = false)
    private Branch sourceBranch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_branch_id", nullable = false)
    private Branch targetBranch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merge_commit_id")
    private Commit mergeCommit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merged_by_id")
    private User mergedBy;

    @Column(name = "merged_at")
    private Instant mergedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closed_by_id")
    private User closedBy;

    @Column(name = "closed_at")
    private Instant closedAt;

    public void merge(User user, Commit commit) {
        status = PullRequestStatus.MERGED;
        mergedBy = user;
        mergeCommit = commit;
        mergedAt = Instant.now();
    }

    public void close(User user) {
        status = PullRequestStatus.CLOSED;
        closedBy = user;
        closedAt = Instant.now();
    }
}
