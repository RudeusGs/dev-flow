package io.devflow.pullrequests.entity;

import io.devflow.common.entity.BaseEntity;
import io.devflow.pullrequests.enums.DiffSide;
import io.devflow.pullrequests.enums.PullRequestCommentType;
import io.devflow.sourcefiles.entity.SourceFile;
import io.devflow.users.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "pull_request_comments")
public class PullRequestComment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pull_request_id", nullable = false)
    private PullRequest pullRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private PullRequestComment parentComment;

    @Enumerated(EnumType.STRING)
    @Column(name = "comment_type", nullable = false, length = 20)
    private PullRequestCommentType commentType = PullRequestCommentType.GENERAL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_file_id")
    private SourceFile sourceFile;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_id")
    private User resolvedBy;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "edited_at")
    private Instant editedAt;

    public void resolve(User user) {
        resolved = true;
        resolvedBy = user;
        resolvedAt = Instant.now();
    }

    public void edit(String body) {
        this.body = body;
        editedAt = Instant.now();
    }
}
