package io.devflow.users.entity;

import io.devflow.common.entity.BaseEntity;
import io.devflow.users.enums.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "display_name", length = 120)
    private String displayName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "location", length = 120)
    private String location;

    @Column(name = "company", length = 120)
    private String company;

    @Column(name = "website_url")
    private String websiteUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "is_verified", nullable = false)
    private boolean verified = false;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "last_active_at")
    private Instant lastActiveAt;

    public void verifyEmail() {
        this.verified = true;
        this.verifiedAt = Instant.now();
    }

    public void markLoginSuccess() {
        Instant now = Instant.now();
        this.lastLoginAt = now;
        this.lastActiveAt = now;
    }

    public void markActive() {
        this.lastActiveAt = Instant.now();
    }

    public boolean canLogin() {
        return this.status == UserStatus.ACTIVE && !isDeleted();
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

    public void suspend() {
        this.status = UserStatus.SUSPENDED;
    }

    public void ban() {
        this.status = UserStatus.BANNED;
    }

    @Override
    public void softDelete() {
        super.softDelete();
        this.status = UserStatus.DELETED;
    }

    @Override
    public void restore() {
        super.restore();
        this.status = UserStatus.ACTIVE;
    }
}