package io.devflow.pullrequests.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePullRequestCommentRequest {
    @NotBlank(message = "Comment body cannot be blank")
    private String body;
    private String filePath;
    private Integer lineNumber;
}
