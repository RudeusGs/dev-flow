package io.devflow;

import io.devflow.activities.entity.ActivityLog;
import io.devflow.auth.entity.EmailVerificationToken;
import io.devflow.auth.entity.PasswordResetToken;
import io.devflow.auth.entity.RefreshToken;
import io.devflow.branches.entity.Branch;
import io.devflow.branches.entity.BranchCommit;
import io.devflow.branches.entity.BranchProtectionRule;
import io.devflow.commits.entity.Commit;
import io.devflow.commits.entity.CommitFileChange;
import io.devflow.commits.entity.CommitParent;
import io.devflow.commits.entity.CommitStatus;
import io.devflow.contributions.entity.ContributionDay;
import io.devflow.contributions.entity.ContributionEvent;
import io.devflow.issues.entity.Issue;
import io.devflow.issues.entity.IssueAssignee;
import io.devflow.issues.entity.IssueComment;
import io.devflow.issues.entity.IssueLabel;
import io.devflow.issues.entity.IssueLabelLink;
import io.devflow.issues.entity.Milestone;
import io.devflow.notifications.entity.Notification;
import io.devflow.organizations.entity.Organization;
import io.devflow.organizations.entity.OrganizationMember;
import io.devflow.organizations.entity.Team;
import io.devflow.organizations.entity.TeamMember;
import io.devflow.organizations.entity.TeamRepository;
import io.devflow.pullrequests.entity.PullRequest;
import io.devflow.pullrequests.entity.PullRequestComment;
import io.devflow.pullrequests.entity.PullRequestCommit;
import io.devflow.pullrequests.entity.PullRequestFileChange;
import io.devflow.pullrequests.entity.PullRequestReview;
import io.devflow.pullrequests.entity.PullRequestReviewRequest;
import io.devflow.reactions.entity.Reaction;
import io.devflow.repos.entity.Repository;
import io.devflow.repos.entity.RepositoryFork;
import io.devflow.repos.entity.RepositoryInvitation;
import io.devflow.repos.entity.RepositoryMember;
import io.devflow.repos.entity.RepositoryRelease;
import io.devflow.repos.entity.RepositoryStar;
import io.devflow.repos.entity.RepositoryTag;
import io.devflow.repos.entity.RepositoryWatch;
import io.devflow.shared.entity.Attachment;
import io.devflow.sourcefiles.entity.FileVersion;
import io.devflow.sourcefiles.entity.SourceFile;
import io.devflow.users.entity.User;
import io.devflow.users.entity.UserFollow;
import io.devflow.webhooks.entity.RepositoryWebhook;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.dialect.PostgreSQLDialect;
import org.junit.jupiter.api.Test;

class EntityMappingsTests {

    @Test
    void validatesAllEntityMappings() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", PostgreSQLDialect.class.getName())
                .applySetting("hibernate.boot.allow_jdbc_metadata_access", false)
                .build();

        try {
            MetadataSources metadataSources = new MetadataSources(registry);

            for (Class<?> entityClass : entityClasses()) {
                metadataSources.addAnnotatedClass(entityClass);
            }

            metadataSources.buildMetadata().buildSessionFactory().close();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    private Class<?>[] entityClasses() {
        return new Class<?>[]{
                User.class,
                RefreshToken.class,
                PasswordResetToken.class,
                EmailVerificationToken.class,
                UserFollow.class,
                Organization.class,
                OrganizationMember.class,
                Team.class,
                TeamMember.class,
                TeamRepository.class,
                Repository.class,
                RepositoryMember.class,
                RepositoryInvitation.class,
                RepositoryStar.class,
                RepositoryWatch.class,
                RepositoryFork.class,
                RepositoryTag.class,
                RepositoryRelease.class,
                Branch.class,
                BranchCommit.class,
                BranchProtectionRule.class,
                SourceFile.class,
                Commit.class,
                CommitParent.class,
                CommitFileChange.class,
                CommitStatus.class,
                ContributionEvent.class,
                ContributionDay.class,
                FileVersion.class,
                Issue.class,
                Milestone.class,
                IssueLabel.class,
                IssueLabelLink.class,
                IssueAssignee.class,
                IssueComment.class,
                PullRequest.class,
                PullRequestCommit.class,
                PullRequestFileChange.class,
                PullRequestComment.class,
                PullRequestReview.class,
                PullRequestReviewRequest.class,
                Notification.class,
                ActivityLog.class,
                Reaction.class,
                Attachment.class,
                RepositoryWebhook.class
        };
    }
}
