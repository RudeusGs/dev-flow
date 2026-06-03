package io.devflow.shared.entity;

import io.devflow.common.entity.CreatedEntity;
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
        name = "attachments",
        indexes = @Index(name = "idx_attachments_target", columnList = "target_type, target_id")
)
public class Attachment extends CreatedEntity {

    @Column(name = "uploaded_by_id")
    private UUID uploadedById;

    @Column(name = "target_type", length = 80)
    private String targetType;

    @Column(name = "target_id")
    private UUID targetId;

    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @Column(name = "stored_filename", length = 255)
    private String storedFilename;

    @Column(name = "storage_provider", nullable = false, length = 50)
    private String storageProvider = "LOCAL";

    @Column(name = "storage_key", columnDefinition = "TEXT")
    private String storageKey;

    @Column(name = "mime_type", length = 120)
    private String mimeType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(name = "checksum_sha256", length = 64)
    private String checksumSha256;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete() {
        deletedAt = Instant.now();
    }
}
