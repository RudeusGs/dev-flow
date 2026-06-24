package io.devflow.issues.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class IssueCommentDto {
    private String id;
    private String issueId;
    private String authorId;
    private String authorUsername;
    private String body;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant editedAt;
}
