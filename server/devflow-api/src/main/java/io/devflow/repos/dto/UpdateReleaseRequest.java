package io.devflow.repos.dto;

import io.devflow.repos.enums.ReleaseStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateReleaseRequest {

    @Size(max = 160, message = "Release name must not exceed 160 characters")
    private String name;

    private String body;
    
    private Boolean prerelease;
    
    private ReleaseStatus status;
}
