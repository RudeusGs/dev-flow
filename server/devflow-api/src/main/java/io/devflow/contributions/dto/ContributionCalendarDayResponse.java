package io.devflow.contributions.dto;

import java.time.LocalDate;

public record ContributionCalendarDayResponse(
        LocalDate date,
        int contributionCount,
        int level,
        int publicCount,
        Integer privateCount,
        ContributionBreakdownResponse breakdown
) {
}
