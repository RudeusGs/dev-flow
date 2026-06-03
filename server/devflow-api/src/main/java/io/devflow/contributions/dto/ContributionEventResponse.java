package io.devflow.contributions.dto;

import io.devflow.contributions.enums.ContributionType;
import io.devflow.contributions.enums.ContributionVisibility;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public record ContributionEventResponse(
        UUID id,
        UUID userId,
        UUID repositoryId,
        ContributionType contributionType,
        ContributionVisibility visibility,
        LocalDate contributionDate,
        Instant occurredAt,
        String sourceType,
        UUID sourceId,
        int weight,
        boolean ignored,
        Map<String, Object> metadata
) {
}
