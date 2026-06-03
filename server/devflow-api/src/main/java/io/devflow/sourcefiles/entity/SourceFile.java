package io.devflow.sourcefiles.entity;

import io.devflow.common.entity.BaseEntity;
import io.devflow.sourcefiles.enums.SourceFileType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
        name = "source_files",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_source_files_branch_path",
                columnNames = {"branch_id", "path"}
        ),
        indexes = {
                @Index(name = "idx_source_files_repository", columnList = "repository_id"),
                @Index(name = "idx_source_files_parent", columnList = "parent_id")
        }
)
public class SourceFile extends BaseEntity {

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "branch_id", nullable = false)
    private UUID branchId;

    @Column(name = "parent_id")
    private UUID parentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false, length = 20)
    private SourceFileType fileType;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "path", nullable = false, columnDefinition = "TEXT")
    private String path;

    @Column(name = "extension", length = 50)
    private String extension;

    @Column(name = "mime_type", length = 120)
    private String mimeType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(name = "content_text", columnDefinition = "TEXT")
    private String contentText;

    @Column(name = "content_sha256", length = 64)
    private String contentSha256;

    @Column(name = "is_binary", nullable = false)
    private boolean binary;

    @Column(name = "created_by_id")
    private UUID createdById;

    @Column(name = "updated_by_id")
    private UUID updatedById;
}
