package io.devflow.repos.repository;

import io.devflow.repos.entity.RepositoryTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RepositoryTagRepository extends JpaRepository<RepositoryTag, UUID> {
    Page<RepositoryTag> findByRepositoryId(UUID repositoryId, Pageable pageable);
    Optional<RepositoryTag> findByRepositoryIdAndName(UUID repositoryId, String name);
    boolean existsByRepositoryIdAndName(UUID repositoryId, String name);
}
