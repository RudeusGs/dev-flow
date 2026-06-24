package io.devflow.repos.controller;

import io.devflow.repos.dto.CreateTagRequest;
import io.devflow.repos.dto.RepositoryTagDto;
import io.devflow.repos.service.RepositoryTagService;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/repos/{ownerUsername}/{repoName}/tags")
public class RepositoryTagController {

    private final RepositoryTagService tagService;
    private final SecurityUtils securityUtils;

    public RepositoryTagController(RepositoryTagService tagService, SecurityUtils securityUtils) {
        this.tagService = tagService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<RepositoryTagDto> createTag(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @Valid @RequestBody CreateTagRequest request) {
        RepositoryTagDto dto = tagService.createTag(userId, ownerUsername, repoName, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<Page<RepositoryTagDto>> listTags(
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            Pageable pageable) {
        UUID currentUserId = securityUtils.getCurrentUserId().orElse(null);
        return ResponseEntity.ok(tagService.listTags(currentUserId, ownerUsername, repoName, pageable));
    }

    @GetMapping("/{tagName}")
    public ResponseEntity<RepositoryTagDto> getTag(
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable String tagName) {
        UUID currentUserId = securityUtils.getCurrentUserId().orElse(null);
        return ResponseEntity.ok(tagService.getTag(currentUserId, ownerUsername, repoName, tagName));
    }

    @DeleteMapping("/{tagName}")
    public ResponseEntity<Void> deleteTag(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable String tagName) {
        tagService.deleteTag(userId, ownerUsername, repoName, tagName);
        return ResponseEntity.noContent().build();
    }
}
