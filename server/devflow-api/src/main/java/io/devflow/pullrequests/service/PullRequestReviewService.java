package io.devflow.pullrequests.service;

import io.devflow.common.exception.ResourceNotFoundException;
import io.devflow.pullrequests.dto.CreatePullRequestReviewRequest;
import io.devflow.pullrequests.dto.PullRequestReviewDto;
import io.devflow.pullrequests.entity.PullRequest;
import io.devflow.pullrequests.entity.PullRequestReview;
import io.devflow.pullrequests.repository.PullRequestRepository;
import io.devflow.pullrequests.repository.PullRequestReviewRepository;
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
public class PullRequestReviewService {

    private final PullRequestReviewRepository reviewRepository;
    private final PullRequestRepository pullRequestRepository;
    private final RepositoryRepository repositoryRepository;
    private final UserRepository userRepository;
    private final RepositoryPermissionService permissionService;

    public PullRequestReviewService(PullRequestReviewRepository reviewRepository,
                                    PullRequestRepository pullRequestRepository,
                                    RepositoryRepository repositoryRepository,
                                    UserRepository userRepository,
                                    RepositoryPermissionService permissionService) {
        this.reviewRepository = reviewRepository;
        this.pullRequestRepository = pullRequestRepository;
        this.repositoryRepository = repositoryRepository;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
    }

    @Transactional
    public PullRequestReviewDto createReview(UUID reviewerId, String ownerUsername, String repoName, int prNumber, CreatePullRequestReviewRequest request) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkReadPermission(reviewerId, repo);

        PullRequest pr = pullRequestRepository.findByRepositoryIdAndPullRequestNumber(repo.getId(), prNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Pull request not found"));

        PullRequestReview review = new PullRequestReview();
        review.setPullRequestId(pr.getId());
        review.setReviewerId(reviewerId);
        review.setStatus(request.getStatus());
        review.setBody(request.getBody());

        PullRequestReview savedReview = reviewRepository.save(review);

        User reviewer = userRepository.findById(reviewerId).orElse(null);
        String reviewerUsername = reviewer != null ? reviewer.getUsername() : "unknown";

        return mapToDto(savedReview, reviewerUsername);
    }

    @Transactional(readOnly = true)
    public Page<PullRequestReviewDto> listReviews(UUID currentUserId, String ownerUsername, String repoName, int prNumber, Pageable pageable) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkReadPermission(currentUserId, repo);

        PullRequest pr = pullRequestRepository.findByRepositoryIdAndPullRequestNumber(repo.getId(), prNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Pull request not found"));

        return reviewRepository.findByPullRequestId(pr.getId(), pageable).map(this::mapToDtoWithLookup);
    }

    private PullRequestReviewDto mapToDtoWithLookup(PullRequestReview review) {
        String reviewerUsername = userRepository.findById(review.getReviewerId())
                .map(User::getUsername)
                .orElse("unknown");
        return mapToDto(review, reviewerUsername);
    }

    private PullRequestReviewDto mapToDto(PullRequestReview review, String reviewerUsername) {
        return PullRequestReviewDto.builder()
                .id(review.getId().toString())
                .pullRequestId(review.getPullRequestId().toString())
                .reviewerId(review.getReviewerId().toString())
                .reviewerUsername(reviewerUsername)
                .status(review.getStatus())
                .body(review.getBody())
                .submittedAt(review.getSubmittedAt())
                .build();
    }
}
