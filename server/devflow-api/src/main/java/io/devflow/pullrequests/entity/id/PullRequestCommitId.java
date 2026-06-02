package io.devflow.pullrequests.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class PullRequestCommitId implements Serializable {

    @Column(name = "pull_request_id", nullable = false)
    private UUID pullRequestId;

    @Column(name = "commit_id", nullable = false)
    private UUID commitId;
}
