package io.devflow.issues.dto;

import io.devflow.issues.enums.MilestoneStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

@Data
public class UpdateMilestoneRequest {

    @Size(max = 160, message = "Milestone title must not exceed 160 characters")
    private String title;

    private String description;
    
    private Instant dueAt;
    
    private MilestoneStatus status;
}
