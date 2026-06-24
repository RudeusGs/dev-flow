package io.devflow.issues.repository;

import io.devflow.issues.entity.IssueComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IssueCommentRepository extends JpaRepository<IssueComment, UUID> {
    Page<IssueComment> findByIssueId(UUID issueId, Pageable pageable);
}
