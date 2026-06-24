package io.devflow.repos.dto;

import io.devflow.repos.enums.RepositoryMemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCollaboratorRoleRequest {
    @NotNull(message = "Role is required")
    private RepositoryMemberRole role;
}
