package io.devflow.issues.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateIssueCommentRequest {
    @NotBlank(message = "Comment body cannot be blank")
    private String body;
}
