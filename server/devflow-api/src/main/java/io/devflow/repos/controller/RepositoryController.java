package io.devflow.repos.controller;

import io.devflow.repos.dto.CreateRepositoryRequest;
import io.devflow.repos.dto.RepositoryResponse;
import io.devflow.repos.service.RepositoryService;
import io.devflow.security.CurrentUser;
import io.devflow.security.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/repos")
public class RepositoryController {

    private final RepositoryService repositoryService;
    private final SecurityUtils securityUtils;

    public RepositoryController(RepositoryService repositoryService, SecurityUtils securityUtils) {
        this.repositoryService = repositoryService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<RepositoryResponse> createRepository(
            @CurrentUser UUID userId,
            @Valid @RequestBody CreateRepositoryRequest request) {
        RepositoryResponse response = repositoryService.createRepository(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<RepositoryResponse>> listPublicRepositories(Pageable pageable) {
        return ResponseEntity.ok(repositoryService.listPublicRepositories(pageable));
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<Page<RepositoryResponse>> listUserRepositories(
            @PathVariable String username,
            Pageable pageable) {
        Optional<UUID> currentUserId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(repositoryService.listUserRepositories(username, currentUserId, pageable));
    }

    @GetMapping("/{ownerUsername}/{repoName}")
    public ResponseEntity<RepositoryResponse> getRepository(
            @PathVariable String ownerUsername,
            @PathVariable String repoName) {
        Optional<UUID> currentUserId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(repositoryService.getRepository(ownerUsername, repoName, currentUserId));
    }

    @DeleteMapping("/{ownerUsername}/{repoName}")
    public ResponseEntity<Void> deleteRepository(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName) {
        repositoryService.deleteRepository(userId, ownerUsername, repoName);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{ownerUsername}/{repoName}/star")
    public ResponseEntity<Void> starRepository(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName) {
        repositoryService.starRepository(userId, ownerUsername, repoName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{ownerUsername}/{repoName}/star")
    public ResponseEntity<Void> unstarRepository(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName) {
        repositoryService.unstarRepository(userId, ownerUsername, repoName);
        return ResponseEntity.noContent().build();
    }
}
