package io.devflow.auth.repository;

import io.devflow.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @org.springframework.data.jpa.repository.Modifying
    int deleteByExpiresAtBefore(java.time.Instant cutoff);
}
