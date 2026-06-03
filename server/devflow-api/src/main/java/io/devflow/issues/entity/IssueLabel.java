package io.devflow.issues.entity;

import io.devflow.common.entity.CreatedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "issue_labels",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_issue_labels_repository_name",
                columnNames = {"repository_id", "name"}
        )
)
public class IssueLabel extends CreatedEntity {

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "name", nullable = false, length = 80)
    private String name;

    @Column(name = "color", nullable = false, length = 20)
    private String color = "#999999";

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_by_id")
    private UUID createdById;
}
