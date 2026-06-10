package io.devflow.repos.repository;

import io.devflow.repos.entity.RepositoryWatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Repository
public interface RepositoryWatchRepository extends JpaRepository<RepositoryWatch, UUID> {
    Optional<RepositoryWatch> findByRepositoryIdAndUserId(UUID repositoryId, UUID userId);
    boolean existsByRepositoryIdAndUserId(UUID repositoryId, UUID userId);
}
