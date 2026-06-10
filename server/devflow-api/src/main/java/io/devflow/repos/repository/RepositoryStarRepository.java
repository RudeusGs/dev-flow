package io.devflow.repos.repository;

import io.devflow.repos.entity.RepositoryStar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Repository
public interface RepositoryStarRepository extends JpaRepository<RepositoryStar, UUID> {
    Optional<RepositoryStar> findByRepositoryIdAndUserId(UUID repositoryId, UUID userId);
    boolean existsByRepositoryIdAndUserId(UUID repositoryId, UUID userId);
    long countByRepositoryId(UUID repositoryId);
}
