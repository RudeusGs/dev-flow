package io.devflow.commits.controller;

import io.devflow.commits.dto.CommitDto;
import io.devflow.commits.dto.CreateCommitRequest;
import io.devflow.commits.service.CommitService;
import io.devflow.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/repos/{ownerUsername}/{repoName}/commits")
public class CommitController {

    private final CommitService commitService;
    private final io.devflow.security.SecurityUtils securityUtils;

    public CommitController(CommitService commitService, io.devflow.security.SecurityUtils securityUtils) {
        this.commitService = commitService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<CommitDto> createCommit(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @Valid @RequestBody CreateCommitRequest request) {
        CommitDto commitDto = commitService.createCommit(userId, ownerUsername, repoName, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(commitDto);
    }

    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<CommitDto>> listCommits(
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String branchName,
            org.springframework.data.domain.Pageable pageable) {
        UUID currentUserId = securityUtils.getCurrentUserId().orElse(null);
        return ResponseEntity.ok(commitService.listCommits(currentUserId, ownerUsername, repoName, branchName, pageable));
    }
}
