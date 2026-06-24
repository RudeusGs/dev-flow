package io.devflow.repos.repository;

import io.devflow.repos.entity.RepositoryRelease;
import io.devflow.repos.enums.ReleaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RepositoryReleaseRepository extends JpaRepository<RepositoryRelease, UUID> {
    Page<RepositoryRelease> findByRepositoryId(UUID repositoryId, Pageable pageable);
    Page<RepositoryRelease> findByRepositoryIdAndStatus(UUID repositoryId, ReleaseStatus status, Pageable pageable);
    Optional<RepositoryRelease> findByRepositoryIdAndTagName(UUID repositoryId, String tagName);
    boolean existsByRepositoryIdAndTagName(UUID repositoryId, String tagName);
}
