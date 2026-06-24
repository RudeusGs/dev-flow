package io.devflow.pullrequests.service;

import io.devflow.branches.entity.Branch;
import io.devflow.branches.repository.BranchRepository;
import io.devflow.commits.entity.Commit;
import io.devflow.commits.repository.CommitRepository;
import io.devflow.common.exception.DuplicateResourceException;
import io.devflow.common.exception.ResourceNotFoundException;
import io.devflow.pullrequests.dto.CreatePullRequestRequest;
import io.devflow.pullrequests.dto.PullRequestDto;
import io.devflow.pullrequests.entity.PullRequest;
import io.devflow.pullrequests.enums.PullRequestStatus;
import io.devflow.pullrequests.repository.PullRequestRepository;
import io.devflow.repos.entity.Repository;
import io.devflow.repos.repository.RepositoryRepository;
import io.devflow.repos.service.RepositoryPermissionService;
import io.devflow.users.entity.User;
import io.devflow.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PullRequestService {

    private final PullRequestRepository pullRequestRepository;
    private final BranchRepository branchRepository;
    private final RepositoryRepository repositoryRepository;
    private final UserRepository userRepository;
    private final RepositoryPermissionService permissionService;
    private final CommitRepository commitRepository;

    public PullRequestService(PullRequestRepository pullRequestRepository,
                              BranchRepository branchRepository,
                              RepositoryRepository repositoryRepository,
                              UserRepository userRepository,
                              RepositoryPermissionService permissionService,
                              CommitRepository commitRepository) {
        this.pullRequestRepository = pullRequestRepository;
        this.branchRepository = branchRepository;
        this.repositoryRepository = repositoryRepository;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
        this.commitRepository = commitRepository;
    }

    @Transactional
    public PullRequestDto createPullRequest(UUID userId, String ownerUsername, String repoName, CreatePullRequestRequest request) {
        Repository repo = getRepositoryAndCheckReadAccess(userId, ownerUsername, repoName);

        Branch sourceBranch = branchRepository.findByRepositoryIdAndName(repo.getId(), request.getSourceBranchName())
                .orElseThrow(() -> new ResourceNotFoundException("Source branch not found"));

        Branch targetBranch = branchRepository.findByRepositoryIdAndName(repo.getId(), request.getTargetBranchName())
                .orElseThrow(() -> new ResourceNotFoundException("Target branch not found"));

        if (sourceBranch.getId().equals(targetBranch.getId())) {
            throw new IllegalArgumentException("Source and target branches cannot be the same");
        }

        if (pullRequestRepository.existsByRepositoryIdAndSourceBranchIdAndTargetBranchIdAndStatus(
                repo.getId(), sourceBranch.getId(), targetBranch.getId(), PullRequestStatus.OPEN)) {
            throw new DuplicateResourceException("An open pull request already exists for these branches");
        }

        int prNumber = repo.getNextPullRequestNumber();
        repo.setNextPullRequestNumber(prNumber + 1);
        repo.setPullRequestsCount(repo.getPullRequestsCount() + 1);
        repositoryRepository.save(repo);

        PullRequest pr = new PullRequest();
        pr.setRepositoryId(repo.getId());
        pr.setPullRequestNumber(prNumber);
        pr.setAuthorId(userId);
        pr.setTitle(request.getTitle());
        pr.setBody(request.getBody());
        pr.setSourceBranchId(sourceBranch.getId());
        pr.setTargetBranchId(targetBranch.getId());
        pr.setStatus(PullRequestStatus.OPEN);

        PullRequest saved = pullRequestRepository.save(pr);
        return mapToDtoWithLookup(saved, sourceBranch.getName(), targetBranch.getName());
    }

    @Transactional(readOnly = true)
    public Page<PullRequestDto> listPullRequests(UUID currentUserId, String ownerUsername, String repoName, PullRequestStatus status, Pageable pageable) {
        Repository repo = getRepositoryAndCheckReadAccess(currentUserId, ownerUsername, repoName);

        Page<PullRequest> prs;
        if (status != null) {
            prs = pullRequestRepository.findByRepositoryIdAndStatus(repo.getId(), status, pageable);
        } else {
            prs = pullRequestRepository.findByRepositoryId(repo.getId(), pageable);
        }

        java.util.Set<UUID> authorIds = prs.stream().map(PullRequest::getAuthorId).filter(java.util.Objects::nonNull).collect(java.util.stream.Collectors.toSet());
        java.util.Map<UUID, String> authorUsernames = userRepository.findByIdIn(authorIds).stream()
                .collect(java.util.stream.Collectors.toMap(User::getId, User::getUsername));

        java.util.Set<UUID> branchIds = new java.util.HashSet<>();
        prs.forEach(pr -> {
            branchIds.add(pr.getSourceBranchId());
            branchIds.add(pr.getTargetBranchId());
        });
        
        java.util.Map<UUID, String> branchNames = branchRepository.findByIdIn(branchIds).stream()
                .collect(java.util.stream.Collectors.toMap(Branch::getId, Branch::getName));

        return prs.map(pr -> {
            String authorUsername = authorUsernames.getOrDefault(pr.getAuthorId(), "unknown");
            String source = branchNames.getOrDefault(pr.getSourceBranchId(), "unknown");
            String target = branchNames.getOrDefault(pr.getTargetBranchId(), "unknown");
            return mapToDto(pr, source, target, authorUsername);
        });
    }

    @Transactional(readOnly = true)
    public PullRequestDto getPullRequest(UUID currentUserId, String ownerUsername, String repoName, int prNumber) {
        Repository repo = getRepositoryAndCheckReadAccess(currentUserId, ownerUsername, repoName);

        PullRequest pr = pullRequestRepository.findByRepositoryIdAndPullRequestNumber(repo.getId(), prNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Pull request not found"));

        Branch source = branchRepository.findById(pr.getSourceBranchId()).orElse(null);
        Branch target = branchRepository.findById(pr.getTargetBranchId()).orElse(null);

        return mapToDtoWithLookup(pr, source != null ? source.getName() : "unknown", target != null ? target.getName() : "unknown");
    }

    @Transactional
    public PullRequestDto mergePullRequest(UUID currentUserId, String ownerUsername, String repoName, int prNumber) {
        Repository repo = getRepositoryAndCheckReadAccess(currentUserId, ownerUsername, repoName);

        if (!permissionService.canWrite(currentUserId, repo)) {
            throw new org.springframework.security.access.AccessDeniedException("You do not have permission to merge this pull request");
        }

        PullRequest pr = pullRequestRepository.findByRepositoryIdAndPullRequestNumber(repo.getId(), prNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Pull request not found"));

        if (pr.getStatus() != PullRequestStatus.OPEN) {
            throw new IllegalArgumentException("Pull request is not open");
        }

        User user = userRepository.findById(currentUserId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Branch source = branchRepository.findById(pr.getSourceBranchId()).orElseThrow(() -> new ResourceNotFoundException("Source branch not found"));
        Branch target = branchRepository.findById(pr.getTargetBranchId()).orElseThrow(() -> new ResourceNotFoundException("Target branch not found"));

        // MVP: create a fake merge commit
        Commit mergeCommit = new Commit();
        mergeCommit.setRepositoryId(repo.getId());
        mergeCommit.setAuthorId(currentUserId);
        mergeCommit.setCommitHash(UUID.randomUUID().toString().replace("-", "").substring(0, 40));
        mergeCommit.setMessage("Merge pull request #" + prNumber + " from " + source.getName());
        mergeCommit.setAuthorName(user.getDisplayName());
        mergeCommit.setAuthorEmail(user.getEmail());
        mergeCommit.setCommitterName(user.getDisplayName());
        mergeCommit.setCommitterEmail(user.getEmail());
        Commit savedCommit = commitRepository.save(mergeCommit);

        // Update target branch head
        target.setHeadCommitId(savedCommit.getId());
        branchRepository.save(target);

        // Mark PR as merged
        pr.merge(currentUserId, savedCommit.getId());
        PullRequest savedPr = pullRequestRepository.save(pr);

        return mapToDtoWithLookup(savedPr, source.getName(), target.getName());
    }

    private Repository getRepositoryAndCheckReadAccess(UUID currentUserId, String ownerUsername, String repoName) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkReadPermission(currentUserId, repo);
        return repo;
    }

    private PullRequestDto mapToDtoWithLookup(PullRequest pr, String sourceBranchName, String targetBranchName) {
        String authorUsername = "unknown";
        if (pr.getAuthorId() != null) {
            authorUsername = userRepository.findById(pr.getAuthorId())
                    .map(User::getUsername)
                    .orElse("unknown");
        }
        return mapToDto(pr, sourceBranchName, targetBranchName, authorUsername);
    }

    private PullRequestDto mapToDto(PullRequest pr, String sourceBranchName, String targetBranchName, String authorUsername) {

        return PullRequestDto.builder()
                .id(pr.getId().toString())
                .repositoryId(pr.getRepositoryId().toString())
                .pullRequestNumber(pr.getPullRequestNumber())
                .authorId(pr.getAuthorId() != null ? pr.getAuthorId().toString() : null)
                .authorUsername(authorUsername)
                .title(pr.getTitle())
                .body(pr.getBody())
                .status(pr.getStatus())
                .sourceBranchName(sourceBranchName)
                .targetBranchName(targetBranchName)
                .mergeCommitId(pr.getMergeCommitId() != null ? pr.getMergeCommitId().toString() : null)
                .createdAt(pr.getCreatedAt())
                .updatedAt(pr.getUpdatedAt())
                .mergedAt(pr.getMergedAt())
                .closedAt(pr.getClosedAt())
                .build();
    }
}
