package io.devflow.issues.controller;

import io.devflow.issues.dto.CreateIssueRequest;
import io.devflow.issues.dto.IssueDto;
import io.devflow.issues.dto.UpdateIssueRequest;
import io.devflow.issues.enums.IssueStatus;
import io.devflow.issues.service.IssueService;
import io.devflow.security.CurrentUser;
import io.devflow.security.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/repos/{ownerUsername}/{repoName}/issues")
public class IssueController {

    private final IssueService issueService;
    private final SecurityUtils securityUtils;

    public IssueController(IssueService issueService, SecurityUtils securityUtils) {
        this.issueService = issueService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<IssueDto> createIssue(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @Valid @RequestBody CreateIssueRequest request) {
        IssueDto issueDto = issueService.createIssue(userId, ownerUsername, repoName, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(issueDto);
    }

    @GetMapping
    public ResponseEntity<Page<IssueDto>> listIssues(
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @RequestParam(required = false) IssueStatus status,
            Pageable pageable) {
        UUID currentUserId = securityUtils.getCurrentUserId().orElse(null);
        return ResponseEntity.ok(issueService.listIssues(currentUserId, ownerUsername, repoName, status, pageable));
    }

    @GetMapping("/{issueNumber}")
    public ResponseEntity<IssueDto> getIssue(
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable int issueNumber) {
        UUID currentUserId = securityUtils.getCurrentUserId().orElse(null);
        return ResponseEntity.ok(issueService.getIssue(currentUserId, ownerUsername, repoName, issueNumber));
    }

    @PatchMapping("/{issueNumber}")
    public ResponseEntity<IssueDto> updateIssue(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable int issueNumber,
            @RequestBody UpdateIssueRequest request) {
        return ResponseEntity.ok(issueService.updateIssue(userId, ownerUsername, repoName, issueNumber, request));
    }
}
