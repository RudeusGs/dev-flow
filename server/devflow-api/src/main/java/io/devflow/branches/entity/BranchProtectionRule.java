package io.devflow.branches.entity;

import io.devflow.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "branch_protection_rules",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_branch_protection_rules_repository_pattern",
                columnNames = {"repository_id", "branch_name_pattern"}
        ),
        indexes = @Index(name = "idx_branch_protection_rules_repository", columnList = "repository_id")
)
public class BranchProtectionRule extends BaseEntity {

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "branch_name_pattern", nullable = false, length = 160)
    private String branchNamePattern;

    @Column(name = "requires_pull_request_review", nullable = false)
    private boolean requiresPullRequestReview;

    @Column(name = "required_approvals", nullable = false)
    private int requiredApprovals;

    @Column(name = "requires_status_checks", nullable = false)
    private boolean requiresStatusChecks;

    @Column(name = "allows_force_pushes", nullable = false)
    private boolean allowsForcePushes;

    @Column(name = "allows_deletions", nullable = false)
    private boolean allowsDeletions;

    @Column(name = "created_by_id")
    private UUID createdById;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "settings", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> settings = new HashMap<>();
}
