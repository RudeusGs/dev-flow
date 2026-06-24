package io.devflow.repos.repository;

import io.devflow.repos.entity.Repository;
import io.devflow.repos.enums.RepositoryOwnerType;
import io.devflow.repos.enums.RepositoryVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Repository
public interface RepositoryRepository extends JpaRepository<Repository, UUID> {
    Optional<Repository> findByOwnerIdAndSlug(UUID ownerId, String slug);
    Page<Repository> findByOwnerId(UUID ownerId, Pageable pageable);
    boolean existsByOwnerIdAndSlug(UUID ownerId, String slug);
    Page<Repository> findByOwnerTypeAndVisibility(RepositoryOwnerType ownerType, RepositoryVisibility visibility, Pageable pageable);

    @Modifying
    @Query("UPDATE Repository r SET r.starsCount = r.starsCount + 1 WHERE r.id = :id")
    void incrementStarsCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Repository r SET r.starsCount = r.starsCount - 1 WHERE r.id = :id")
    void decrementStarsCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Repository r SET r.forksCount = r.forksCount + 1 WHERE r.id = :id")
    void incrementForksCount(@Param("id") UUID id);

    @Query("SELECT r FROM Repository r WHERE r.ownerId = :ownerId AND (r.visibility = 'PUBLIC' OR r.ownerId = :userId OR r.id IN (SELECT rm.repositoryId FROM RepositoryMember rm WHERE rm.userId = :userId))")
    Page<Repository> findUserVisibleRepositories(@Param("ownerId") UUID ownerId, @Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT r FROM Repository r WHERE r.ownerId = :ownerId AND r.visibility = 'PUBLIC'")
    Page<Repository> findUserPublicRepositories(@Param("ownerId") UUID ownerId, Pageable pageable);
}
