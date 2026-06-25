package io.devflow.commits.service;

import io.devflow.branches.entity.Branch;
import io.devflow.branches.repository.BranchRepository;
import io.devflow.commits.dto.CommitDto;
import io.devflow.commits.dto.CreateCommitRequest;
import io.devflow.commits.entity.Commit;
import io.devflow.commits.repository.CommitRepository;
import io.devflow.common.exception.ResourceNotFoundException;
import io.devflow.repos.entity.Repository;
import io.devflow.repos.repository.RepositoryRepository;
import io.devflow.repos.service.RepositoryPermissionService;
import io.devflow.sourcefiles.entity.SourceFile;
import io.devflow.sourcefiles.enums.SourceFileType;
import io.devflow.sourcefiles.repository.SourceFileRepository;
import io.devflow.users.entity.User;
import io.devflow.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.Optional;

@Service
public class CommitService {

    private final CommitRepository commitRepository;
    private final BranchRepository branchRepository;
    private final RepositoryRepository repositoryRepository;
    private final UserRepository userRepository;
    private final RepositoryPermissionService permissionService;
    private final SourceFileRepository sourceFileRepository;
    private final io.devflow.repos.service.GitManagerService gitManagerService;

    public CommitService(CommitRepository commitRepository,
                         BranchRepository branchRepository,
                         RepositoryRepository repositoryRepository,
                         UserRepository userRepository,
                         RepositoryPermissionService permissionService,
                         SourceFileRepository sourceFileRepository,
                         io.devflow.repos.service.GitManagerService gitManagerService) {
        this.commitRepository = commitRepository;
        this.branchRepository = branchRepository;
        this.repositoryRepository = repositoryRepository;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
        this.sourceFileRepository = sourceFileRepository;
        this.gitManagerService = gitManagerService;
    }

    @Transactional
    public CommitDto createCommit(UUID userId, String ownerUsername, String repoName, CreateCommitRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        if (!permissionService.canWrite(userId, repo)) {
            throw new org.springframework.security.access.AccessDeniedException("You do not have write access to this repository");
        }

        String branchName = request.getBranchName() != null ? request.getBranchName() : repo.getDefaultBranchName();
        Branch branch = branchRepository.findByRepositoryIdAndName(repo.getId(), branchName)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found: " + branchName));

        Commit commit = new Commit();
        commit.setRepositoryId(repo.getId());
        commit.setAuthorId(user.getId());
        commit.setCommitHash(UUID.randomUUID().toString().replace("-", "").substring(0, 40));
        commit.setMessage(request.getMessage());
        commit.setAuthorName(user.getDisplayName());
        commit.setAuthorEmail(user.getEmail());
        commit.setCommitterName(user.getDisplayName());
        commit.setCommitterEmail(user.getEmail());

        Commit savedCommit = commitRepository.save(commit);

        branch.setHeadCommitId(savedCommit.getId());
        branchRepository.save(branch);

        if (request.getFilePath() != null && !request.getFilePath().isEmpty()) {
            Optional<SourceFile> existingFileOpt = sourceFileRepository.findByBranchIdAndPath(branch.getId(), request.getFilePath());
            if (request.isDelete()) {
                existingFileOpt.ifPresent(sourceFileRepository::delete);
            } else {
                SourceFile file = existingFileOpt.orElseGet(SourceFile::new);
                file.setRepositoryId(repo.getId());
                file.setBranchId(branch.getId());
                file.setFileType(SourceFileType.FILE);
                file.setName(getFileNameFromPath(request.getFilePath()));
                file.setPath(request.getFilePath());
                file.setSizeBytes(request.getFileContent() != null ? request.getFileContent().getBytes().length : 0);
                file.setBinary(false);
                file.setCreatedById(user.getId());
                file.setUpdatedById(user.getId());
                sourceFileRepository.save(file);
            }
        }

        return mapToDto(savedCommit);
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<CommitDto> listCommits(UUID currentUserId, String ownerUsername, String repoName, String branchName, org.springframework.data.domain.Pageable pageable) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkReadPermission(currentUserId, repo);

        // Limit the number of commits fetched from JGit to 50 for now
        java.util.List<CommitDto> jgitCommits = gitManagerService.listCommits(ownerUsername, repoName, branchName, 50);

        if (!jgitCommits.isEmpty()) {
            return new org.springframework.data.domain.PageImpl<>(jgitCommits, pageable, jgitCommits.size());
        }

        // Fallback to DB (Mock data) if JGit repo is empty or doesn't exist
        return commitRepository.findByRepositoryIdOrderByCommittedAtDesc(repo.getId(), pageable).map(this::mapToDto);
    }

    private String getFileNameFromPath(String path) {
        int lastSlash = path.lastIndexOf('/');
        return lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
    }

    private CommitDto mapToDto(Commit commit) {
        return CommitDto.builder()
                .id(commit.getId().toString())
                .repositoryId(commit.getRepositoryId().toString())
                .commitHash(commit.getCommitHash())
                .message(commit.getMessage())
                .authorName(commit.getAuthorName())
                .authorEmail(commit.getAuthorEmail())
                .committerName(commit.getCommitterName())
                .committerEmail(commit.getCommitterEmail())
                .authoredAt(commit.getAuthoredAt())
                .committedAt(commit.getCommittedAt())
                .build();
    }
}
