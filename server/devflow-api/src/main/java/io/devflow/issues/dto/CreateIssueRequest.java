package io.devflow.issues.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateIssueRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String body;
}
