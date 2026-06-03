package io.devflow.contributions.entity;

import io.devflow.common.entity.CreatedEntity;
import io.devflow.contributions.enums.ContributionCategory;
import io.devflow.contributions.enums.ContributionType;
import io.devflow.contributions.enums.ContributionVisibility;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "contribution_days",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_contribution_days_user_date",
                columnNames = {"user_id", "contribution_date"}
        ),
        indexes = @Index(name = "idx_contribution_days_date", columnList = "contribution_date")
)
public class ContributionDay extends CreatedEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "contribution_date", nullable = false)
    private LocalDate contributionDate;

    @Column(name = "total_count", nullable = false)
    private int totalCount;

    @Column(name = "public_count", nullable = false)
    private int publicCount;

    @Column(name = "private_count", nullable = false)
    private int privateCount;

    @Column(name = "commit_count", nullable = false)
    private int commitCount;

    @Column(name = "issue_count", nullable = false)
    private int issueCount;

    @Column(name = "pull_request_count", nullable = false)
    private int pullRequestCount;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Column(name = "comment_count", nullable = false)
    private int commentCount;

    @Column(name = "release_count", nullable = false)
    private int releaseCount;

    @Column(name = "repository_count", nullable = false)
    private int repositoryCount;

    @Column(name = "other_count", nullable = false)
    private int otherCount;

    public void addContribution(ContributionType type, ContributionVisibility visibility, int weight) {
        int contributionWeight = Math.max(weight, 1);
        totalCount += contributionWeight;

        if (visibility == ContributionVisibility.PRIVATE) {
            privateCount += contributionWeight;
        } else {
            publicCount += contributionWeight;
        }

        incrementCategory(type.getCategory(), contributionWeight);
    }

    private void incrementCategory(ContributionCategory category, int weight) {
        switch (category) {
            case COMMIT -> commitCount += weight;
            case ISSUE -> issueCount += weight;
            case PULL_REQUEST -> pullRequestCount += weight;
            case REVIEW -> reviewCount += weight;
            case COMMENT -> commentCount += weight;
            case RELEASE -> releaseCount += weight;
            case REPOSITORY -> repositoryCount += weight;
            case OTHER -> otherCount += weight;
        }
    }
}
