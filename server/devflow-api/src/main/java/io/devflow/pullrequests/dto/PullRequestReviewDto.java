package io.devflow.pullrequests.dto;

import io.devflow.pullrequests.enums.PullRequestReviewStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PullRequestReviewDto {
    private String id;
    private String pullRequestId;
    private String reviewerId;
    private String reviewerUsername;
    private PullRequestReviewStatus status;
    private String body;
    private Instant submittedAt;
}
