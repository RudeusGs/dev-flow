package io.devflow.repos.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class RepositoryTagDto {
    private String id;
    private String repositoryId;
    private String name;
    private String commitId;
    private String taggedById;
    private String taggedByUsername;
    private String message;
    private Instant createdAt;
}
