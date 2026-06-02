package io.devflow.pullrequests.entity;

import io.devflow.commits.entity.Commit;
import io.devflow.pullrequests.entity.id.PullRequestCommitId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "pull_request_commits")
public class PullRequestCommit {

    @EmbeddedId
    private PullRequestCommitId id = new PullRequestCommitId();

    @MapsId("pullRequestId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pull_request_id", nullable = false)
    private PullRequest pullRequest;

    @MapsId("commitId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "commit_id", nullable = false)
    private Commit commit;
}
