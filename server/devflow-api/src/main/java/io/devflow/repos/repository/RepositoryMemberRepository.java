package io.devflow.repos.repository;

import io.devflow.repos.entity.RepositoryMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Repository
public interface RepositoryMemberRepository extends JpaRepository<RepositoryMember, UUID> {
    Optional<RepositoryMember> findByRepositoryIdAndUserId(UUID repositoryId, UUID userId);
    List<RepositoryMember> findByRepositoryId(UUID repositoryId);
    boolean existsByRepositoryIdAndUserId(UUID repositoryId, UUID userId);
    List<RepositoryMember> findByUserId(UUID userId);
}
