package io.devflow.users.repository;

import io.devflow.users.entity.UserFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, UUID> {
    Optional<UserFollow> findByFollowerIdAndFollowingId(UUID followerId, UUID followingId);
    boolean existsByFollowerIdAndFollowingId(UUID followerId, UUID followingId);
    long countByFollowerId(UUID followerId);
    long countByFollowingId(UUID followingId);
    Page<UserFollow> findByFollowerId(UUID followerId, Pageable pageable);
    Page<UserFollow> findByFollowingId(UUID followingId, Pageable pageable);
}
