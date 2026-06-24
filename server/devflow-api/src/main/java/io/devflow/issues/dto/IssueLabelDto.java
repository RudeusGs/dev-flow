package io.devflow.issues.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class IssueLabelDto {
    private String id;
    private String repositoryId;
    private String name;
    private String color;
    private String description;
    private Instant createdAt;
}
