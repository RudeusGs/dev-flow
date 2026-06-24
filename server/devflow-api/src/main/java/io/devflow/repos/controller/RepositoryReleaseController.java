package io.devflow.repos.controller;

import io.devflow.repos.dto.CreateReleaseRequest;
import io.devflow.repos.dto.RepositoryReleaseDto;
import io.devflow.repos.dto.UpdateReleaseRequest;
import io.devflow.repos.service.RepositoryReleaseService;
import io.devflow.security.CurrentUser;
import io.devflow.security.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/repos/{ownerUsername}/{repoName}/releases")
public class RepositoryReleaseController {

    private final RepositoryReleaseService releaseService;
    private final SecurityUtils securityUtils;

    public RepositoryReleaseController(RepositoryReleaseService releaseService, SecurityUtils securityUtils) {
        this.releaseService = releaseService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<RepositoryReleaseDto> createRelease(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @Valid @RequestBody CreateReleaseRequest request) {
        RepositoryReleaseDto dto = releaseService.createRelease(userId, ownerUsername, repoName, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<Page<RepositoryReleaseDto>> listReleases(
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            Pageable pageable) {
        UUID currentUserId = securityUtils.getCurrentUserId().orElse(null);
        return ResponseEntity.ok(releaseService.listReleases(currentUserId, ownerUsername, repoName, pageable));
    }

    @GetMapping("/tags/{tagName}")
    public ResponseEntity<RepositoryReleaseDto> getReleaseByTagName(
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable String tagName) {
        UUID currentUserId = securityUtils.getCurrentUserId().orElse(null);
        return ResponseEntity.ok(releaseService.getRelease(currentUserId, ownerUsername, repoName, tagName));
    }

    @PatchMapping("/tags/{tagName}")
    public ResponseEntity<RepositoryReleaseDto> updateRelease(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable String tagName,
            @Valid @RequestBody UpdateReleaseRequest request) {
        return ResponseEntity.ok(releaseService.updateRelease(userId, ownerUsername, repoName, tagName, request));
    }

    @DeleteMapping("/tags/{tagName}")
    public ResponseEntity<Void> deleteRelease(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable String tagName) {
        releaseService.deleteRelease(userId, ownerUsername, repoName, tagName);
        return ResponseEntity.noContent().build();
    }
}
