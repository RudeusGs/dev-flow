package io.devflow.pullrequests.service;

import io.devflow.common.exception.ResourceNotFoundException;
import io.devflow.pullrequests.dto.CreatePullRequestCommentRequest;
import io.devflow.pullrequests.dto.PullRequestCommentDto;
import io.devflow.pullrequests.entity.PullRequest;
import io.devflow.pullrequests.entity.PullRequestComment;
import io.devflow.pullrequests.repository.PullRequestCommentRepository;
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
public class PullRequestCommentService {

    private final PullRequestCommentRepository commentRepository;
    private final PullRequestRepository pullRequestRepository;
    private final RepositoryRepository repositoryRepository;
    private final UserRepository userRepository;
    private final RepositoryPermissionService permissionService;

    public PullRequestCommentService(PullRequestCommentRepository commentRepository,
                                     PullRequestRepository pullRequestRepository,
                                     RepositoryRepository repositoryRepository,
                                     UserRepository userRepository,
                                     RepositoryPermissionService permissionService) {
        this.commentRepository = commentRepository;
        this.pullRequestRepository = pullRequestRepository;
        this.repositoryRepository = repositoryRepository;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
    }

    @Transactional
    public PullRequestCommentDto createComment(UUID authorId, String ownerUsername, String repoName, int prNumber, CreatePullRequestCommentRequest request) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkReadPermission(authorId, repo);

        PullRequest pr = pullRequestRepository.findByRepositoryIdAndPullRequestNumber(repo.getId(), prNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Pull request not found"));

        PullRequestComment comment = new PullRequestComment();
        comment.setPullRequestId(pr.getId());
        comment.setAuthorId(authorId);
        comment.setBody(request.getBody());
        
        comment.setFilePath(request.getFilePath());
        comment.setLineNumber(request.getLineNumber());

        PullRequestComment savedComment = commentRepository.save(comment);

        User author = userRepository.findById(authorId).orElse(null);
        String authorUsername = author != null ? author.getUsername() : "unknown";

        return mapToDto(savedComment, authorUsername);
    }

    @Transactional(readOnly = true)
    public Page<PullRequestCommentDto> listComments(UUID currentUserId, String ownerUsername, String repoName, int prNumber, Pageable pageable) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkReadPermission(currentUserId, repo);

        PullRequest pr = pullRequestRepository.findByRepositoryIdAndPullRequestNumber(repo.getId(), prNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Pull request not found"));

        return commentRepository.findByPullRequestId(pr.getId(), pageable).map(this::mapToDtoWithLookup);
    }

    private PullRequestCommentDto mapToDtoWithLookup(PullRequestComment comment) {
        String authorUsername = userRepository.findById(comment.getAuthorId())
                .map(User::getUsername)
                .orElse("unknown");
        return mapToDto(comment, authorUsername);
    }

    private PullRequestCommentDto mapToDto(PullRequestComment comment, String authorUsername) {
        return PullRequestCommentDto.builder()
                .id(comment.getId().toString())
                .pullRequestId(comment.getPullRequestId().toString())
                .authorId(comment.getAuthorId() != null ? comment.getAuthorId().toString() : null)
                .authorUsername(authorUsername)
                .body(comment.getBody())
                .filePath(comment.getFilePath())
                .lineNumber(comment.getLineNumber())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
