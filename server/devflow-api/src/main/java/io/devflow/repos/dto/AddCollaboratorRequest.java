package io.devflow.repos.dto;

import io.devflow.repos.enums.RepositoryMemberRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCollaboratorRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotNull(message = "Role is required")
    private RepositoryMemberRole role = RepositoryMemberRole.CONTRIBUTOR;
}
