package io.devflow.branches.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBranchRequest {

    @NotBlank(message = "Branch name is required")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Branch name can only contain alphanumeric characters, hyphens, underscores, and periods")
    private String name;

    private String fromBranchName;
}
