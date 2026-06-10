package io.devflow.repos.repository;

import io.devflow.repos.entity.Repository;
import io.devflow.repos.enums.RepositoryOwnerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Repository
public interface RepositoryRepository extends JpaRepository<Repository, UUID> {
    Optional<Repository> findByOwnerIdAndSlug(UUID ownerId, String slug);
    Page<Repository> findByOwnerId(UUID ownerId, Pageable pageable);
    boolean existsByOwnerIdAndSlug(UUID ownerId, String slug);
    Page<Repository> findByOwnerTypeAndVisibility(RepositoryOwnerType ownerType, io.devflow.repos.enums.RepositoryVisibility visibility, Pageable pageable);
}
