package io.devflow.issues.repository;

import io.devflow.issues.entity.Milestone;
import io.devflow.issues.enums.MilestoneStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, UUID> {
    Page<Milestone> findByRepositoryId(UUID repositoryId, Pageable pageable);
    Page<Milestone> findByRepositoryIdAndStatus(UUID repositoryId, MilestoneStatus status, Pageable pageable);
    Optional<Milestone> findByRepositoryIdAndTitleIgnoreCase(UUID repositoryId, String title);
    boolean existsByRepositoryIdAndTitleIgnoreCase(UUID repositoryId, String title);
}
