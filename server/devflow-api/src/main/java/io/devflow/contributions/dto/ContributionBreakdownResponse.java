package io.devflow.contributions.dto;

public record ContributionBreakdownResponse(
        int commits,
        int issues,
        int pullRequests,
        int reviews,
        int comments,
        int releases,
        int repositories,
        int other
) {
}
