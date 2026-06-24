package io.devflow.sourcefiles.service;

import io.devflow.branches.entity.Branch;
import io.devflow.branches.repository.BranchRepository;
import io.devflow.common.exception.ResourceNotFoundException;
import io.devflow.repos.entity.Repository;
import io.devflow.repos.repository.RepositoryRepository;
import io.devflow.repos.service.RepositoryPermissionService;
import io.devflow.sourcefiles.dto.SourceFileDto;
import io.devflow.sourcefiles.entity.SourceFile;
import io.devflow.sourcefiles.repository.SourceFileRepository;
import io.devflow.users.entity.User;
import io.devflow.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SourceFileService {

    private final SourceFileRepository sourceFileRepository;
    private final BranchRepository branchRepository;
    private final RepositoryRepository repositoryRepository;
    private final UserRepository userRepository;
    private final RepositoryPermissionService permissionService;
    private final io.devflow.repos.service.GitManagerService gitManagerService;

    public SourceFileService(SourceFileRepository sourceFileRepository,
                             BranchRepository branchRepository,
                             RepositoryRepository repositoryRepository,
                             UserRepository userRepository,
                             RepositoryPermissionService permissionService,
                             io.devflow.repos.service.GitManagerService gitManagerService) {
        this.sourceFileRepository = sourceFileRepository;
        this.branchRepository = branchRepository;
        this.repositoryRepository = repositoryRepository;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
        this.gitManagerService = gitManagerService;
    }

    @Transactional(readOnly = true)
    public List<SourceFileDto> listFiles(UUID currentUserId, String ownerUsername, String repoName, String branchName, String path) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkReadPermission(currentUserId, repo);

        List<SourceFileDto> jgitFiles = gitManagerService.listFiles(ownerUsername, repoName, branchName, path);
        if (!jgitFiles.isEmpty()) {
            return jgitFiles;
        }

        // Fallback to database for empty repo or mock data
        Branch branch = branchRepository.findByRepositoryIdAndName(repo.getId(), branchName)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found: " + branchName));

        List<SourceFile> files;
        if (path == null || path.isEmpty() || path.equals("/")) {
            files = sourceFileRepository.findByBranchIdAndParentIdIsNull(branch.getId());
        } else {
            SourceFile parent = sourceFileRepository.findByBranchIdAndPath(branch.getId(), path)
                    .orElseThrow(() -> new ResourceNotFoundException("Path not found"));
            files = sourceFileRepository.findByBranchIdAndParentId(branch.getId(), parent.getId());
            
            // if it's a direct file request instead of a directory
            if (files.isEmpty() && parent.getFileType() == io.devflow.sourcefiles.enums.SourceFileType.FILE) {
                files = List.of(parent);
            }
        }

        return files.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private SourceFileDto mapToDto(SourceFile file) {
        return SourceFileDto.builder()
                .id(file.getId().toString())
                .name(file.getName())
                .path(file.getPath())
                .fileType(file.getFileType())
                .sizeBytes(file.getSizeBytes())
                .binary(file.isBinary())
                .build();
    }
}
