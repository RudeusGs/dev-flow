package io.devflow.sourcefiles.repository;

import io.devflow.sourcefiles.entity.SourceFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SourceFileRepository extends JpaRepository<SourceFile, UUID> {
    Optional<SourceFile> findByBranchIdAndPath(UUID branchId, String path);
    List<SourceFile> findByBranchIdAndParentId(UUID branchId, UUID parentId);
    List<SourceFile> findByBranchIdAndParentIdIsNull(UUID branchId);
}
