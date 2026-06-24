package io.devflow.pullrequests.dto;

import io.devflow.pullrequests.enums.PullRequestReviewStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePullRequestReviewRequest {
    @NotNull(message = "Review status is required")
    private PullRequestReviewStatus status;
    private String body;
}
