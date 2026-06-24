package io.devflow.issues.controller;

import io.devflow.issues.dto.CreateIssueCommentRequest;
import io.devflow.issues.dto.IssueCommentDto;
import io.devflow.issues.service.IssueCommentService;
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
@RequestMapping("/api/v1/repos/{ownerUsername}/{repoName}/issues/{issueNumber}/comments")
public class IssueCommentController {

    private final IssueCommentService issueCommentService;
    private final SecurityUtils securityUtils;

    public IssueCommentController(IssueCommentService issueCommentService, SecurityUtils securityUtils) {
        this.issueCommentService = issueCommentService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<IssueCommentDto> createComment(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable int issueNumber,
            @Valid @RequestBody CreateIssueCommentRequest request) {
        IssueCommentDto dto = issueCommentService.createComment(userId, ownerUsername, repoName, issueNumber, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<Page<IssueCommentDto>> listComments(
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable int issueNumber,
            Pageable pageable) {
        UUID currentUserId = securityUtils.getCurrentUserId().orElse(null);
        return ResponseEntity.ok(issueCommentService.listComments(currentUserId, ownerUsername, repoName, issueNumber, pageable));
    }
}
