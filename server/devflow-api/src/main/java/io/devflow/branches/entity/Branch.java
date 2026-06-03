package io.devflow.branches.entity;

import io.devflow.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "branches",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_branches_repository_name",
                columnNames = {"repository_id", "name"}
        )
)
public class Branch extends BaseEntity {

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "created_from_branch_id")
    private UUID createdFromBranchId;

    @Column(name = "head_commit_id")
    private UUID headCommitId;

    @Column(name = "created_by_id")
    private UUID createdById;

    @Column(name = "is_default", nullable = false)
    private boolean defaultBranch;

    @Column(name = "is_protected", nullable = false)
    private boolean protectedBranch;
}
