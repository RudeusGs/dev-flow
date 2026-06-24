package io.devflow.commits.repository;

import io.devflow.commits.entity.Commit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommitRepository extends JpaRepository<Commit, UUID> {
    Optional<Commit> findByRepositoryIdAndCommitHash(UUID repositoryId, String commitHash);
    Page<Commit> findByRepositoryId(UUID repositoryId, Pageable pageable);
    Page<Commit> findByRepositoryIdOrderByCommittedAtDesc(UUID repositoryId, Pageable pageable);
}
