package io.devflow.repos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTagRequest {
    @NotBlank(message = "Tag name is required")
    @Size(max = 120, message = "Tag name must not exceed 120 characters")
    private String name;

    @NotBlank(message = "Commit ID is required")
    private String commitId;

    private String message;
}
