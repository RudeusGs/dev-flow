package io.devflow.repos.dto;

import io.devflow.repos.enums.RepositoryVisibility;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateRepositoryRequest {

    @Size(max = 100, message = "Repository name must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Repository name can only contain alphanumeric characters, hyphens, underscores, and periods")
    private String name;

    private String description;

    private RepositoryVisibility visibility;

    private String defaultBranch;
}
