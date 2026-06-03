package io.devflow.sourcefiles.entity;

import io.devflow.common.entity.CreatedEntity;
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
        name = "file_versions",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_file_versions_commit_path",
                columnNames = {"commit_id", "path"}
        ),
        indexes = {
                @Index(name = "idx_file_versions_repository", columnList = "repository_id"),
                @Index(name = "idx_file_versions_source_file", columnList = "source_file_id")
        }
)
public class FileVersion extends CreatedEntity {

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "source_file_id")
    private UUID sourceFileId;

    @Column(name = "commit_id", nullable = false)
    private UUID commitId;

    @Column(name = "path", nullable = false, columnDefinition = "TEXT")
    private String path;

    @Column(name = "file_mode", length = 20)
    private String fileMode;

    @Column(name = "blob_hash", length = 80)
    private String blobHash;

    @Column(name = "content_sha256", length = 64)
    private String contentSha256;

    @Column(name = "object_storage_key", columnDefinition = "TEXT")
    private String objectStorageKey;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;
}
