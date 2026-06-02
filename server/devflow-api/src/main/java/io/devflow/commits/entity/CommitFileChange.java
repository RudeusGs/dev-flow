package io.devflow.commits.entity;

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
@Table(name = "commit_file_changes")
public class CommitFileChange extends CreatedEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "commit_id", nullable = false)
    private Commit commit;

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

    @Column(name = "old_content_sha256", length = 64)
    private String oldContentSha256;

    @Column(name = "new_content_sha256", length = 64)
    private String newContentSha256;

    @Column(name = "additions", nullable = false)
    private int additions;

    @Column(name = "deletions", nullable = false)
    private int deletions;

    @Column(name = "patch_text", columnDefinition = "TEXT")
    private String patchText;
}
