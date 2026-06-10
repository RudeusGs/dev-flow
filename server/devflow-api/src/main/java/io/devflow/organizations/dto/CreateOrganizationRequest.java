package io.devflow.organizations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrganizationRequest {

    @NotBlank(message = "Organization name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Name can only contain alphanumeric characters, hyphens, underscores, and periods")
    private String name;

    @Size(max = 120, message = "Display name is too long")
    private String displayName;

    @Size(max = 500, message = "Description is too long")
    private String description;

    private String email;
}
