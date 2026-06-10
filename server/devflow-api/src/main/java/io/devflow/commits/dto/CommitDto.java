package io.devflow.commits.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class CommitDto {
    private String id;
    private String repositoryId;
    private String commitHash;
    private String message;
    private String authorName;
    private String authorEmail;
    private String committerName;
    private String committerEmail;
    private Instant authoredAt;
    private Instant committedAt;
}
