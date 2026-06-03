package io.devflow.issues.entity;

import io.devflow.common.entity.UuidEntity;
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
        name = "issue_label_links",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_issue_label_links_issue_label",
                columnNames = {"issue_id", "label_id"}
        ),
        indexes = @Index(name = "idx_issue_label_links_label", columnList = "label_id")
)
public class IssueLabelLink extends UuidEntity {

    @Column(name = "issue_id", nullable = false)
    private UUID issueId;

    @Column(name = "label_id", nullable = false)
    private UUID labelId;
}
