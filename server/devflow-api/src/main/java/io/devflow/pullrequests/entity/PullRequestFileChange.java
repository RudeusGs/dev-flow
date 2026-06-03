package io.devflow.pullrequests.entity;

import io.devflow.commits.enums.FileChangeType;
import io.devflow.common.entity.CreatedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "pull_request_file_changes",
        indexes = {
                @Index(name = "idx_pull_request_file_changes_repository", columnList = "repository_id"),
                @Index(name = "idx_pull_request_file_changes_pull_request", columnList = "pull_request_id"),
                @Index(name = "idx_pull_request_file_changes_source_file", columnList = "source_file_id")
        }
)
public class PullRequestFileChange extends CreatedEntity {

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "pull_request_id", nullable = false)
    private UUID pullRequestId;

    @Column(name = "source_file_id")
    private UUID sourceFileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false, length = 20)
    private FileChangeType changeType;

    @Column(name = "old_path", columnDefinition = "TEXT")
    private String oldPath;

    @Column(name = "new_path", columnDefinition = "TEXT")
    private String newPath;

    @Column(name = "old_blob_hash", length = 80)
    private String oldBlobHash;

    @Column(name = "new_blob_hash", length = 80)
    private String newBlobHash;

    @Column(name = "additions", nullable = false)
    private int additions;

    @Column(name = "deletions", nullable = false)
    private int deletions;

    @Column(name = "diff_hunk_count", nullable = false)
    private int diffHunkCount;

    @Column(name = "patch_storage_key", columnDefinition = "TEXT")
    private String patchStorageKey;
}
