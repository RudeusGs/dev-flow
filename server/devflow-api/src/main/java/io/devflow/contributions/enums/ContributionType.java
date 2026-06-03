package io.devflow.contributions.enums;

public enum ContributionType {
    COMMIT_CREATED(ContributionCategory.COMMIT),
    ISSUE_OPENED(ContributionCategory.ISSUE),
    ISSUE_CLOSED(ContributionCategory.ISSUE),
    PULL_REQUEST_OPENED(ContributionCategory.PULL_REQUEST),
    PULL_REQUEST_MERGED(ContributionCategory.PULL_REQUEST),
    PULL_REQUEST_REVIEWED(ContributionCategory.REVIEW),
    ISSUE_COMMENT_CREATED(ContributionCategory.COMMENT),
    PULL_REQUEST_COMMENT_CREATED(ContributionCategory.COMMENT),
    REPOSITORY_CREATED(ContributionCategory.REPOSITORY),
    RELEASE_PUBLISHED(ContributionCategory.RELEASE),
    OTHER(ContributionCategory.OTHER);

    private final ContributionCategory category;

    ContributionType(ContributionCategory category) {
        this.category = category;
    }

    public ContributionCategory getCategory() {
        return category;
    }
}
