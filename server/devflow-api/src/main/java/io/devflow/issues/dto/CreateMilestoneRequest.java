package io.devflow.issues.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

@Data
public class CreateMilestoneRequest {
    @NotBlank(message = "Milestone title is required")
    @Size(max = 160, message = "Milestone title must not exceed 160 characters")
    private String title;

    private String description;
    
    private Instant dueAt;
}
