package io.devflow.branches.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BranchDto {
    private String id;
    private String name;
    private String repositoryId;
    private String headCommitId;
    private boolean defaultBranch;
    private boolean protectedBranch;
}
