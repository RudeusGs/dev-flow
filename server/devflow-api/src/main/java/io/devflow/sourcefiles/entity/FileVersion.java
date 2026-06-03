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
                name = "uk_file_versions_source_file_commit",
                columnNames = {"source_file_id", "commit_id"}
        ),
        indexes = @Index(name = "idx_file_versions_commit", columnList = "commit_id")
)
public class FileVersion extends CreatedEntity {

    @Column(name = "source_file_id", nullable = false)
    private UUID sourceFileId;

    @Column(name = "commit_id", nullable = false)
    private UUID commitId;

    @Column(name = "path", nullable = false, columnDefinition = "TEXT")
    private String path;

    @Column(name = "content_text", columnDefinition = "TEXT")
    private String contentText;

    @Column(name = "content_sha256", length = 64)
    private String contentSha256;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;
}
