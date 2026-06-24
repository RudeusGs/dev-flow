package io.devflow.issues.service;

import io.devflow.common.exception.ResourceNotFoundException;
import io.devflow.issues.dto.CreateIssueCommentRequest;
import io.devflow.issues.dto.IssueCommentDto;
import io.devflow.issues.entity.Issue;
import io.devflow.issues.entity.IssueComment;
import io.devflow.issues.repository.IssueCommentRepository;
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
public class IssueCommentService {

    private final IssueCommentRepository issueCommentRepository;
    private final IssueRepository issueRepository;
    private final RepositoryRepository repositoryRepository;
    private final UserRepository userRepository;
    private final RepositoryPermissionService permissionService;

    public IssueCommentService(IssueCommentRepository issueCommentRepository,
                               IssueRepository issueRepository,
                               RepositoryRepository repositoryRepository,
                               UserRepository userRepository,
                               RepositoryPermissionService permissionService) {
        this.issueCommentRepository = issueCommentRepository;
        this.issueRepository = issueRepository;
        this.repositoryRepository = repositoryRepository;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
    }

    @Transactional
    public IssueCommentDto createComment(UUID authorId, String ownerUsername, String repoName, int issueNumber, CreateIssueCommentRequest request) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkReadPermission(authorId, repo);

        Issue issue = issueRepository.findByRepositoryIdAndIssueNumber(repo.getId(), issueNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found"));

        IssueComment comment = new IssueComment();
        comment.setIssueId(issue.getId());
        comment.setAuthorId(authorId);
        comment.setBody(request.getBody());

        IssueComment savedComment = issueCommentRepository.save(comment);
        
        User author = userRepository.findById(authorId).orElse(null);
        String authorUsername = author != null ? author.getUsername() : "unknown";

        return mapToDto(savedComment, authorUsername);
    }

    @Transactional(readOnly = true)
    public Page<IssueCommentDto> listComments(UUID currentUserId, String ownerUsername, String repoName, int issueNumber, Pageable pageable) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkReadPermission(currentUserId, repo);

        Issue issue = issueRepository.findByRepositoryIdAndIssueNumber(repo.getId(), issueNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found"));

        return issueCommentRepository.findByIssueId(issue.getId(), pageable).map(this::mapToDtoWithLookup);
    }

    private IssueCommentDto mapToDtoWithLookup(IssueComment comment) {
        String authorUsername = userRepository.findById(comment.getAuthorId())
                .map(User::getUsername)
                .orElse("unknown");
        return mapToDto(comment, authorUsername);
    }

    private IssueCommentDto mapToDto(IssueComment comment, String authorUsername) {
        return IssueCommentDto.builder()
                .id(comment.getId().toString())
                .issueId(comment.getIssueId().toString())
                .authorId(comment.getAuthorId() != null ? comment.getAuthorId().toString() : null)
                .authorUsername(authorUsername)
                .body(comment.getBody())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .editedAt(comment.getEditedAt())
                .build();
    }
}
