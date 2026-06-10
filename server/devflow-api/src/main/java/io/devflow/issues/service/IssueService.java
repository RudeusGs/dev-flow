package io.devflow.issues.service;

import io.devflow.common.exception.ResourceNotFoundException;
import io.devflow.issues.dto.CreateIssueRequest;
import io.devflow.issues.dto.IssueDto;
import io.devflow.issues.dto.UpdateIssueRequest;
import io.devflow.issues.entity.Issue;
import io.devflow.issues.enums.IssueStatus;
import io.devflow.issues.repository.IssueRepository;
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
public class IssueService {

    private final IssueRepository issueRepository;
    private final RepositoryRepository repositoryRepository;
    private final UserRepository userRepository;
    private final RepositoryPermissionService permissionService;

    public IssueService(IssueRepository issueRepository,
                        RepositoryRepository repositoryRepository,
                        UserRepository userRepository,
                        RepositoryPermissionService permissionService) {
        this.issueRepository = issueRepository;
        this.repositoryRepository = repositoryRepository;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
    }

    @Transactional
    public IssueDto createIssue(UUID userId, String ownerUsername, String repoName, CreateIssueRequest request) {
        Repository repo = getRepositoryAndCheckReadAccess(userId, ownerUsername, repoName);
        
        // Anyone who can read the repo can create an issue for now
        
        int issueNumber = repo.getNextIssueNumber();
        repo.setNextIssueNumber(issueNumber + 1);
        repo.setIssuesCount(repo.getIssuesCount() + 1);
        repositoryRepository.save(repo);

        Issue issue = new Issue();
        issue.setRepositoryId(repo.getId());
        issue.setIssueNumber(issueNumber);
        issue.setAuthorId(userId);
        issue.setTitle(request.getTitle());
        issue.setBody(request.getBody());
        issue.setStatus(IssueStatus.OPEN);

        Issue saved = issueRepository.save(issue);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<IssueDto> listIssues(UUID currentUserId, String ownerUsername, String repoName, IssueStatus status, Pageable pageable) {
        Repository repo = getRepositoryAndCheckReadAccess(currentUserId, ownerUsername, repoName);

        Page<Issue> issues;
        if (status != null) {
            issues = issueRepository.findByRepositoryIdAndStatus(repo.getId(), status, pageable);
        } else {
            issues = issueRepository.findByRepositoryId(repo.getId(), pageable);
        }

        return issues.map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public IssueDto getIssue(UUID currentUserId, String ownerUsername, String repoName, int issueNumber) {
        Repository repo = getRepositoryAndCheckReadAccess(currentUserId, ownerUsername, repoName);

        Issue issue = issueRepository.findByRepositoryIdAndIssueNumber(repo.getId(), issueNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found"));

        return mapToDto(issue);
    }

    @Transactional
    public IssueDto updateIssue(UUID currentUserId, String ownerUsername, String repoName, int issueNumber, UpdateIssueRequest request) {
        Repository repo = getRepositoryAndCheckReadAccess(currentUserId, ownerUsername, repoName);

        Issue issue = issueRepository.findByRepositoryIdAndIssueNumber(repo.getId(), issueNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found"));

        // Only author or repo maintainers can update
        if (!issue.getAuthorId().equals(currentUserId) && !permissionService.canWrite(currentUserId, repo)) {
            throw new org.springframework.security.access.AccessDeniedException("You do not have permission to update this issue");
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            issue.setTitle(request.getTitle());
        }
        if (request.getBody() != null) {
            issue.setBody(request.getBody());
        }
        if (request.getStatus() != null && issue.getStatus() != request.getStatus()) {
            if (request.getStatus() == IssueStatus.CLOSED) {
                issue.close(currentUserId, null);
                repo.setIssuesCount(Math.max(0, repo.getIssuesCount() - 1));
            } else if (request.getStatus() == IssueStatus.OPEN) {
                issue.reopen();
                repo.setIssuesCount(repo.getIssuesCount() + 1);
            }
            repositoryRepository.save(repo);
        }

        Issue updated = issueRepository.save(issue);
        return mapToDto(updated);
    }

    private Repository getRepositoryAndCheckReadAccess(UUID currentUserId, String ownerUsername, String repoName) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkReadPermission(currentUserId, repo);
        return repo;
    }

    private IssueDto mapToDto(Issue issue) {
        String authorUsername = "unknown";
        if (issue.getAuthorId() != null) {
            authorUsername = userRepository.findById(issue.getAuthorId())
                    .map(User::getUsername)
                    .orElse("unknown");
        }

        return IssueDto.builder()
                .id(issue.getId().toString())
                .repositoryId(issue.getRepositoryId().toString())
                .issueNumber(issue.getIssueNumber())
                .authorId(issue.getAuthorId() != null ? issue.getAuthorId().toString() : null)
                .authorUsername(authorUsername)
                .title(issue.getTitle())
                .body(issue.getBody())
                .status(issue.getStatus())
                .createdAt(issue.getCreatedAt())
                .updatedAt(issue.getUpdatedAt())
                .closedAt(issue.getClosedAt())
                .build();
    }
}
