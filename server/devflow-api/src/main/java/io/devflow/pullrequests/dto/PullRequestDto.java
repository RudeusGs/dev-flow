package io.devflow.pullrequests.dto;

import io.devflow.pullrequests.enums.PullRequestStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class PullRequestDto {
    private String id;
    private String repositoryId;
    private int pullRequestNumber;
    private String authorId;
    private String authorUsername;
    private String title;
    private String body;
    private PullRequestStatus status;
    private String sourceBranchName;
    private String targetBranchName;
    private String mergeCommitId;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant mergedAt;
    private Instant closedAt;
}
