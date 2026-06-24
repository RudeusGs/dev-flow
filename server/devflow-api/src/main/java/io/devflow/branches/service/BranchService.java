package io.devflow.branches.service;

import io.devflow.branches.dto.BranchDto;
import io.devflow.branches.dto.CreateBranchRequest;
import io.devflow.branches.entity.Branch;
import io.devflow.branches.repository.BranchRepository;
import io.devflow.common.exception.DuplicateResourceException;
import io.devflow.common.exception.ResourceNotFoundException;
import io.devflow.repos.entity.Repository;
import io.devflow.repos.repository.RepositoryRepository;
import io.devflow.repos.service.RepositoryPermissionService;
import io.devflow.users.entity.User;
import io.devflow.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BranchService {

    private final BranchRepository branchRepository;
    private final RepositoryRepository repositoryRepository;
    private final UserRepository userRepository;
    private final RepositoryPermissionService permissionService;
    private final io.devflow.repos.service.GitManagerService gitManagerService;

    public BranchService(BranchRepository branchRepository,
                         RepositoryRepository repositoryRepository,
                         UserRepository userRepository,
                         RepositoryPermissionService permissionService,
                         io.devflow.repos.service.GitManagerService gitManagerService) {
        this.branchRepository = branchRepository;
        this.repositoryRepository = repositoryRepository;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
        this.gitManagerService = gitManagerService;
    }

    @Transactional(readOnly = true)
    public List<BranchDto> listBranches(UUID currentUserId, String ownerUsername, String repoName) {
        Repository repo = getRepositoryAndCheckReadAccess(currentUserId, ownerUsername, repoName);

        List<BranchDto> jgitBranches = gitManagerService.listBranches(ownerUsername, repoName, repo.getDefaultBranchName());
        if (!jgitBranches.isEmpty()) {
            // Merge with DB data if necessary, or just return JGit data
            return jgitBranches;
        }

        return branchRepository.findByRepositoryId(repo.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BranchDto createBranch(UUID currentUserId, String ownerUsername, String repoName, CreateBranchRequest request) {
        Repository repo = getRepositoryAndCheckWriteAccess(currentUserId, ownerUsername, repoName);

        if (branchRepository.existsByRepositoryIdAndName(repo.getId(), request.getName())) {
            throw new DuplicateResourceException("Branch already exists");
        }

        Branch baseBranch = null;
        if (request.getFromBranchName() != null) {
            baseBranch = branchRepository.findByRepositoryIdAndName(repo.getId(), request.getFromBranchName())
                    .orElseThrow(() -> new ResourceNotFoundException("Source branch not found"));
        } else {
            baseBranch = branchRepository.findByRepositoryIdAndDefaultBranchTrue(repo.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Default branch not found to fork from"));
        }

        Branch newBranch = new Branch();
        newBranch.setRepositoryId(repo.getId());
        newBranch.setName(request.getName());
        newBranch.setCreatedFromBranchId(baseBranch.getId());
        newBranch.setHeadCommitId(baseBranch.getHeadCommitId());
        newBranch.setCreatedById(currentUserId);
        newBranch.setDefaultBranch(false);
        newBranch.setProtectedBranch(false);

        Branch saved = branchRepository.save(newBranch);
        return mapToDto(saved);
    }

    @Transactional
    public void deleteBranch(UUID currentUserId, String ownerUsername, String repoName, String branchName) {
        Repository repo = getRepositoryAndCheckWriteAccess(currentUserId, ownerUsername, repoName);

        Branch branch = branchRepository.findByRepositoryIdAndName(repo.getId(), branchName)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        if (branch.isDefaultBranch()) {
            throw new IllegalArgumentException("Cannot delete default branch");
        }

        if (branch.isProtectedBranch() && !permissionService.canAdmin(currentUserId, repo)) {
            throw new org.springframework.security.access.AccessDeniedException("Cannot delete protected branch without admin rights");
        }

        branchRepository.delete(branch);
    }

    private Repository getRepositoryAndCheckReadAccess(UUID currentUserId, String ownerUsername, String repoName) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkReadPermission(currentUserId, repo);
        return repo;
    }

    private Repository getRepositoryAndCheckWriteAccess(UUID currentUserId, String ownerUsername, String repoName) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        if (!permissionService.canWrite(currentUserId, repo)) {
            throw new org.springframework.security.access.AccessDeniedException("You do not have write access to this repository");
        }
        return repo;
    }

    private BranchDto mapToDto(Branch branch) {
        return BranchDto.builder()
                .id(branch.getId().toString())
                .name(branch.getName())
                .repositoryId(branch.getRepositoryId().toString())
                .headCommitId(branch.getHeadCommitId() != null ? branch.getHeadCommitId().toString() : null)
                .defaultBranch(branch.isDefaultBranch())
                .protectedBranch(branch.isProtectedBranch())
                .build();
    }
}
