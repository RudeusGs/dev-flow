package io.devflow.pullrequests.controller;

import io.devflow.pullrequests.dto.CreatePullRequestCommentRequest;
import io.devflow.pullrequests.dto.PullRequestCommentDto;
import io.devflow.pullrequests.service.PullRequestCommentService;
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
@RequestMapping("/api/v1/repos/{ownerUsername}/{repoName}/pulls/{prNumber}/comments")
public class PullRequestCommentController {

    private final PullRequestCommentService commentService;
    private final SecurityUtils securityUtils;

    public PullRequestCommentController(PullRequestCommentService commentService, SecurityUtils securityUtils) {
        this.commentService = commentService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<PullRequestCommentDto> createComment(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable int prNumber,
            @Valid @RequestBody CreatePullRequestCommentRequest request) {
        PullRequestCommentDto dto = commentService.createComment(userId, ownerUsername, repoName, prNumber, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<Page<PullRequestCommentDto>> listComments(
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable int prNumber,
            Pageable pageable) {
        UUID currentUserId = securityUtils.getCurrentUserId().orElse(null);
        return ResponseEntity.ok(commentService.listComments(currentUserId, ownerUsername, repoName, prNumber, pageable));
    }
}
