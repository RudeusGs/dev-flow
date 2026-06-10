package io.devflow.issues.dto;

import io.devflow.issues.enums.IssueStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class IssueDto {
    private String id;
    private String repositoryId;
    private int issueNumber;
    private String authorId;
    private String authorUsername;
    private String title;
    private String body;
    private IssueStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant closedAt;
}
