package io.devflow.issues.repository;

import io.devflow.issues.entity.IssueLabel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IssueLabelRepository extends JpaRepository<IssueLabel, UUID> {
    Page<IssueLabel> findByRepositoryId(UUID repositoryId, Pageable pageable);
    Optional<IssueLabel> findByRepositoryIdAndNameIgnoreCase(UUID repositoryId, String name);
    boolean existsByRepositoryIdAndNameIgnoreCase(UUID repositoryId, String name);
}
