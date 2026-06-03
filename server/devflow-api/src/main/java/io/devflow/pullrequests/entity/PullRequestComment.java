package io.devflow.pullrequests.entity;

import io.devflow.common.entity.BaseEntity;
import io.devflow.pullrequests.enums.DiffSide;
import io.devflow.pullrequests.enums.PullRequestCommentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
        name = "pull_request_comments",
        indexes = {
                @Index(name = "idx_pull_request_comments_pull_request", columnList = "pull_request_id"),
                @Index(name = "idx_pull_request_comments_author", columnList = "author_id"),
                @Index(name = "idx_pull_request_comments_source_file", columnList = "source_file_id")
        }
)
public class PullRequestComment extends BaseEntity {

    @Column(name = "pull_request_id", nullable = false)
    private UUID pullRequestId;

    @Column(name = "author_id")
    private UUID authorId;

    @Column(name = "parent_comment_id")
    private UUID parentCommentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "comment_type", nullable = false, length = 20)
    private PullRequestCommentType commentType = PullRequestCommentType.GENERAL;

    @Column(name = "source_file_id")
    private UUID sourceFileId;

    @Column(name = "file_path", columnDefinition = "TEXT")
    private String filePath;

    @Column(name = "line_number")
    private Integer lineNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "diff_side", length = 20)
    private DiffSide diffSide;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "is_resolved", nullable = false)
    private boolean resolved;

    @Column(name = "resolved_by_id")
    private UUID resolvedById;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "edited_at")
    private Instant editedAt;

    public void resolve(UUID resolvedById) {
        resolved = true;
        this.resolvedById = resolvedById;
        resolvedAt = Instant.now();
    }

    public void edit(String body) {
        this.body = body;
        editedAt = Instant.now();
    }
}
