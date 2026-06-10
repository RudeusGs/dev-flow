package io.devflow.issues.repository;

import io.devflow.issues.entity.Issue;
import io.devflow.issues.enums.IssueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IssueRepository extends JpaRepository<Issue, UUID> {
    Optional<Issue> findByRepositoryIdAndIssueNumber(UUID repositoryId, int issueNumber);
    Page<Issue> findByRepositoryId(UUID repositoryId, Pageable pageable);
    Page<Issue> findByRepositoryIdAndStatus(UUID repositoryId, IssueStatus status, Pageable pageable);
}
