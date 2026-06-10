package io.devflow.pullrequests.repository;

import io.devflow.pullrequests.entity.PullRequest;
import io.devflow.pullrequests.enums.PullRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PullRequestRepository extends JpaRepository<PullRequest, UUID> {
    Optional<PullRequest> findByRepositoryIdAndPullRequestNumber(UUID repositoryId, int pullRequestNumber);
    Page<PullRequest> findByRepositoryId(UUID repositoryId, Pageable pageable);
    Page<PullRequest> findByRepositoryIdAndStatus(UUID repositoryId, PullRequestStatus status, Pageable pageable);
    boolean existsByRepositoryIdAndSourceBranchIdAndTargetBranchIdAndStatus(UUID repositoryId, UUID sourceBranchId, UUID targetBranchId, PullRequestStatus status);
}
