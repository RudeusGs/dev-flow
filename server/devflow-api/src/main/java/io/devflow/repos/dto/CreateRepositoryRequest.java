package io.devflow.repos.dto;

import io.devflow.repos.enums.RepositoryVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRepositoryRequest {

    @NotBlank(message = "Repository name is required")
    @Size(min = 1, max = 100, message = "Repository name must be between 1 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Repository name can only contain alphanumeric characters, hyphens, underscores, and periods")
    private String name;

    @Size(max = 500, message = "Description is too long")
    private String description;

    private RepositoryVisibility visibility = RepositoryVisibility.PUBLIC;
    
    // Optional organization ID. If null, the repository belongs to the current user.
    private String organizationId;
    
    private boolean createReadme = true;
}
