package io.devflow.users.entity;

import io.devflow.common.entity.CreatedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "user_follows",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_follows_follower_following",
                columnNames = {"follower_id", "following_id"}
        ),
        indexes = @Index(name = "idx_user_follows_following", columnList = "following_id")
)
public class UserFollow extends CreatedEntity {

    @Column(name = "follower_id", nullable = false)
    private UUID followerId;

    @Column(name = "following_id", nullable = false)
    private UUID followingId;
}
