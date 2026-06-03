package io.devflow.organizations.entity;

import io.devflow.common.entity.BaseEntity;
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
        name = "organizations",
        uniqueConstraints = @UniqueConstraint(name = "uk_organizations_slug", columnNames = "slug"),
        indexes = @Index(name = "idx_organizations_created_by", columnList = "created_by_id")
)
public class Organization extends BaseEntity {

    @Column(name = "created_by_id")
    private UUID createdById;

    @Column(name = "slug", nullable = false, length = 100)
    private String slug;

    @Column(name = "display_name", nullable = false, length = 160)
    private String displayName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(name = "website_url", columnDefinition = "TEXT")
    private String websiteUrl;

    @Column(name = "location", length = 120)
    private String location;

    @Column(name = "billing_email", length = 255)
    private String billingEmail;
}
