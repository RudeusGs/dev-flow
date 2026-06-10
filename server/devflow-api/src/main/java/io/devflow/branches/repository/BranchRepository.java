package io.devflow.branches.repository;

import io.devflow.branches.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BranchRepository extends JpaRepository<Branch, UUID> {
    Optional<Branch> findByRepositoryIdAndName(UUID repositoryId, String name);
    List<Branch> findByRepositoryId(UUID repositoryId);
    boolean existsByRepositoryIdAndName(UUID repositoryId, String name);
    Optional<Branch> findByRepositoryIdAndDefaultBranchTrue(UUID repositoryId);
}
