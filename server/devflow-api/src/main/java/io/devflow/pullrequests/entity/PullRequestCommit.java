package io.devflow.pullrequests.entity;

import io.devflow.common.entity.UuidEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
        name = "pull_request_commits",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_pull_request_commits_pull_request_commit",
                columnNames = {"pull_request_id", "commit_id"}
        ),
        indexes = @Index(name = "idx_pull_request_commits_commit", columnList = "commit_id")
)
public class PullRequestCommit extends UuidEntity {

    @Column(name = "pull_request_id", nullable = false)
    private UUID pullRequestId;

    @Column(name = "commit_id", nullable = false)
    private UUID commitId;
}
