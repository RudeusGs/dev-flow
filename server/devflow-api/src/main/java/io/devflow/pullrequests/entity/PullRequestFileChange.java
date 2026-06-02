package io.devflow.pullrequests.entity;

import io.devflow.commits.enums.FileChangeType;
import io.devflow.common.entity.CreatedEntity;
import io.devflow.sourcefiles.entity.SourceFile;
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

@Getter
@Setter
@Entity
@Table(name = "pull_request_file_changes")
public class PullRequestFileChange extends CreatedEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pull_request_id", nullable = false)
    private PullRequest pullRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_file_id")
    private SourceFile sourceFile;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false, length = 20)
    private FileChangeType changeType;

    @Column(name = "old_path", columnDefinition = "TEXT")
    private String oldPath;

    @Column(name = "new_path", columnDefinition = "TEXT")
    private String newPath;

    @Column(name = "additions", nullable = false)
    private int additions;

    @Column(name = "deletions", nullable = false)
    private int deletions;

    @Column(name = "patch_text", columnDefinition = "TEXT")
    private String patchText;
}
