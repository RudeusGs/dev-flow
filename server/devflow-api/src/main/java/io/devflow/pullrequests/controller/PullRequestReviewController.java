package io.devflow.pullrequests.controller;

import io.devflow.pullrequests.dto.CreatePullRequestReviewRequest;
import io.devflow.pullrequests.dto.PullRequestReviewDto;
import io.devflow.pullrequests.service.PullRequestReviewService;
import io.devflow.security.CurrentUser;
import io.devflow.security.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/repos/{ownerUsername}/{repoName}/pulls/{prNumber}/reviews")
public class PullRequestReviewController {

    private final PullRequestReviewService reviewService;
    private final SecurityUtils securityUtils;

    public PullRequestReviewController(PullRequestReviewService reviewService, SecurityUtils securityUtils) {
        this.reviewService = reviewService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<PullRequestReviewDto> createReview(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable int prNumber,
            @Valid @RequestBody CreatePullRequestReviewRequest request) {
        PullRequestReviewDto dto = reviewService.createReview(userId, ownerUsername, repoName, prNumber, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<Page<PullRequestReviewDto>> listReviews(
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable int prNumber,
            Pageable pageable) {
        UUID currentUserId = securityUtils.getCurrentUserId().orElse(null);
        return ResponseEntity.ok(reviewService.listReviews(currentUserId, ownerUsername, repoName, prNumber, pageable));
    }
}
