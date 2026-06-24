package io.devflow.repos.dto;

import io.devflow.repos.enums.RepositoryMemberRole;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class RepositoryMemberDto {
    private String id;
    private String repositoryId;
    private String userId;
    private String username;
    private String email;
    private String avatarUrl;
    private RepositoryMemberRole role;
    private Instant joinedAt;
}
