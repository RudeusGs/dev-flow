package io.devflow.repos.dto;

import io.devflow.repos.enums.ReleaseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateReleaseRequest {
    @NotBlank(message = "Tag name is required")
    @Size(max = 120, message = "Tag name must not exceed 120 characters")
    private String tagName;

    @NotBlank(message = "Release name is required")
    @Size(max = 160, message = "Release name must not exceed 160 characters")
    private String name;

    private String body;
    
    private boolean prerelease;
    
    private ReleaseStatus status = ReleaseStatus.PUBLISHED;
}
