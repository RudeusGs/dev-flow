package io.devflow.issues.entity;

import io.devflow.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "issue_comments",
        indexes = {
                @Index(name = "idx_issue_comments_issue", columnList = "issue_id"),
                @Index(name = "idx_issue_comments_author", columnList = "author_id")
        }
)
public class IssueComment extends BaseEntity {

    @Column(name = "issue_id", nullable = false)
    private UUID issueId;

    @Column(name = "author_id")
    private UUID authorId;

    @Column(name = "parent_comment_id")
    private UUID parentCommentId;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "edited_at")
    private Instant editedAt;

    public void edit(String body) {
        this.body = body;
        editedAt = Instant.now();
    }
}
