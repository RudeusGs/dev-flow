package io.devflow.pullrequests.controller;

import io.devflow.pullrequests.dto.CreatePullRequestRequest;
import io.devflow.pullrequests.dto.PullRequestDto;
import io.devflow.pullrequests.enums.PullRequestStatus;
import io.devflow.pullrequests.service.PullRequestService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/repos/{ownerUsername}/{repoName}/pulls")
public class PullRequestController {

    private final PullRequestService pullRequestService;
    private final SecurityUtils securityUtils;

    public PullRequestController(PullRequestService pullRequestService, SecurityUtils securityUtils) {
        this.pullRequestService = pullRequestService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<PullRequestDto> createPullRequest(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @Valid @RequestBody CreatePullRequestRequest request) {
        PullRequestDto dto = pullRequestService.createPullRequest(userId, ownerUsername, repoName, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<Page<PullRequestDto>> listPullRequests(
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @RequestParam(required = false) PullRequestStatus status,
            Pageable pageable) {
        UUID currentUserId = securityUtils.getCurrentUserId().orElse(null);
        return ResponseEntity.ok(pullRequestService.listPullRequests(currentUserId, ownerUsername, repoName, status, pageable));
    }

    @GetMapping("/{prNumber}")
    public ResponseEntity<PullRequestDto> getPullRequest(
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable int prNumber) {
        UUID currentUserId = securityUtils.getCurrentUserId().orElse(null);
        return ResponseEntity.ok(pullRequestService.getPullRequest(currentUserId, ownerUsername, repoName, prNumber));
    }

    @PostMapping("/{prNumber}/merge")
    public ResponseEntity<PullRequestDto> mergePullRequest(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable int prNumber) {
        return ResponseEntity.ok(pullRequestService.mergePullRequest(userId, ownerUsername, repoName, prNumber));
    }
}
