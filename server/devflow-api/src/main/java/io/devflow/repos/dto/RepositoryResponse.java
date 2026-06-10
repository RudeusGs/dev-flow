package io.devflow.repos.dto;

import io.devflow.repos.enums.RepositoryOwnerType;
import io.devflow.repos.enums.RepositoryVisibility;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class RepositoryResponse {
    private String id;
    private String name;
    private String slug;
    private String description;
    private RepositoryVisibility visibility;
    private RepositoryOwnerType ownerType;
    private String ownerId;
    private String ownerUsername; // useful for frontend
    private String defaultBranchName;
    private int starsCount;
    private int forksCount;
    private int issuesCount;
    private Instant createdAt;
    private Instant updatedAt;
}
