package io.devflow.contributions.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ContributionCalendarResponse(
        UUID userId,
        LocalDate from,
        LocalDate to,
        int totalContributions,
        boolean includesPrivateContributions,
        List<ContributionCalendarDayResponse> days
) {
}
