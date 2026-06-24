package io.devflow.pullrequests.repository;

import io.devflow.pullrequests.entity.PullRequestReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PullRequestReviewRepository extends JpaRepository<PullRequestReview, UUID> {
    Page<PullRequestReview> findByPullRequestId(UUID pullRequestId, Pageable pageable);
}
