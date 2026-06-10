package io.devflow.pullrequests.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePullRequestRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String body;

    @NotBlank(message = "Source branch is required")
    private String sourceBranchName;

    @NotBlank(message = "Target branch is required")
    private String targetBranchName;
}
