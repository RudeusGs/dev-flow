package io.devflow.pullrequests.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PullRequestCommentDto {
    private String id;
    private String pullRequestId;
    private String authorId;
    private String authorUsername;
    private String body;
    private String filePath;
    private Integer lineNumber;
    private Instant createdAt;
    private Instant updatedAt;
}
