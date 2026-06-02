package io.devflow;

import io.devflow.activities.entity.ActivityLog;
import io.devflow.auth.entity.EmailVerificationToken;
import io.devflow.auth.entity.PasswordResetToken;
import io.devflow.auth.entity.RefreshToken;
import io.devflow.branches.entity.Branch;
import io.devflow.commits.entity.Commit;
import io.devflow.commits.entity.CommitFileChange;
import io.devflow.issues.entity.Issue;
import io.devflow.issues.entity.IssueAssignee;
import io.devflow.issues.entity.IssueComment;
import io.devflow.issues.entity.IssueLabel;
import io.devflow.issues.entity.IssueLabelLink;
import io.devflow.notifications.entity.Notification;
import io.devflow.pullrequests.entity.PullRequest;
import io.devflow.pullrequests.entity.PullRequestComment;
import io.devflow.pullrequests.entity.PullRequestCommit;
import io.devflow.pullrequests.entity.PullRequestFileChange;
import io.devflow.pullrequests.entity.PullRequestReview;
import io.devflow.reactions.entity.Reaction;
import io.devflow.repos.entity.Repository;
import io.devflow.repos.entity.RepositoryFork;
import io.devflow.repos.entity.RepositoryInvitation;
import io.devflow.repos.entity.RepositoryMember;
import io.devflow.repos.entity.RepositoryStar;
import io.devflow.shared.entity.Attachment;
import io.devflow.sourcefiles.entity.FileVersion;
import io.devflow.sourcefiles.entity.SourceFile;
import io.devflow.users.entity.User;
import io.devflow.users.entity.UserFollow;
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
                Repository.class,
                RepositoryMember.class,
                RepositoryInvitation.class,
                RepositoryStar.class,
                RepositoryFork.class,
                Branch.class,
                SourceFile.class,
                Commit.class,
                CommitFileChange.class,
                FileVersion.class,
                Issue.class,
                IssueLabel.class,
                IssueLabelLink.class,
                IssueAssignee.class,
                IssueComment.class,
                PullRequest.class,
                PullRequestCommit.class,
                PullRequestFileChange.class,
                PullRequestComment.class,
                PullRequestReview.class,
                Notification.class,
                ActivityLog.class,
                Reaction.class,
                Attachment.class
        };
    }
}
