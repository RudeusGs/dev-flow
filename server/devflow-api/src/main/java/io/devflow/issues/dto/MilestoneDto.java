package io.devflow.issues.dto;

import io.devflow.issues.enums.MilestoneStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class MilestoneDto {
    private String id;
    private String repositoryId;
    private String title;
    private String description;
    private MilestoneStatus status;
    private Instant dueAt;
    private Instant closedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
