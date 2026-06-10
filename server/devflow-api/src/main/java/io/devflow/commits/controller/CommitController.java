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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/repos/{ownerUsername}/{repoName}/commits")
public class CommitController {

    private final CommitService commitService;

    public CommitController(CommitService commitService) {
        this.commitService = commitService;
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
}
