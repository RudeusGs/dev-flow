package io.devflow.branches.entity;

import io.devflow.commits.entity.Commit;
import io.devflow.common.entity.BaseEntity;
import io.devflow.repos.entity.Repository;
import io.devflow.users.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "branches")
public class Branch extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_from_branch_id")
    private Branch createdFromBranch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_commit_id")
    private Commit headCommit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @Column(name = "is_default", nullable = false)
    private boolean defaultBranch;

    @Column(name = "is_protected", nullable = false)
    private boolean protectedBranch;
}
