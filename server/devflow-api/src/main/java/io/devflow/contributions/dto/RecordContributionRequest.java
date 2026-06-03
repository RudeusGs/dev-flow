package io.devflow.contributions.dto;

import io.devflow.contributions.enums.ContributionType;
import io.devflow.contributions.enums.ContributionVisibility;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record RecordContributionRequest(
        @NotNull UUID userId,
        UUID repositoryId,
        @NotNull ContributionType contributionType,
        ContributionVisibility visibility,
        Instant occurredAt,
        @NotBlank String sourceType,
        @NotNull UUID sourceId,
        @Min(1) Integer weight,
        Map<String, Object> metadata
) {
}
