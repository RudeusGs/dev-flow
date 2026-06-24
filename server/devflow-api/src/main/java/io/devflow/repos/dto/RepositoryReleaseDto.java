package io.devflow.repos.dto;

import io.devflow.repos.enums.ReleaseStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class RepositoryReleaseDto {
    private String id;
    private String repositoryId;
    private String tagId;
    private String tagName;
    private String authorId;
    private String authorUsername;
    private String name;
    private String body;
    private ReleaseStatus status;
    private boolean prerelease;
    private Instant publishedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
