package io.devflow.repos.repository;

import io.devflow.repos.entity.RepositoryFork;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RepositoryForkRepository extends JpaRepository<RepositoryFork, UUID> {
    Page<RepositoryFork> findBySourceRepositoryId(UUID sourceRepositoryId, Pageable pageable);
}
