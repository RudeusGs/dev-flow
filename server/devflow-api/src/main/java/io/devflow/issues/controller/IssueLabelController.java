package io.devflow.issues.controller;

import io.devflow.issues.dto.CreateLabelRequest;
import io.devflow.issues.dto.IssueLabelDto;
import io.devflow.issues.dto.UpdateLabelRequest;
import io.devflow.issues.service.IssueLabelService;
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
@RequestMapping("/api/v1/repos/{ownerUsername}/{repoName}/labels")
public class IssueLabelController {

    private final IssueLabelService issueLabelService;
    private final SecurityUtils securityUtils;

    public IssueLabelController(IssueLabelService issueLabelService, SecurityUtils securityUtils) {
        this.issueLabelService = issueLabelService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<IssueLabelDto> createLabel(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @Valid @RequestBody CreateLabelRequest request) {
        IssueLabelDto dto = issueLabelService.createLabel(userId, ownerUsername, repoName, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<Page<IssueLabelDto>> listLabels(
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            Pageable pageable) {
        UUID currentUserId = securityUtils.getCurrentUserId().orElse(null);
        return ResponseEntity.ok(issueLabelService.listLabels(currentUserId, ownerUsername, repoName, pageable));
    }

    @PatchMapping("/{labelName}")
    public ResponseEntity<IssueLabelDto> updateLabel(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable String labelName,
            @Valid @RequestBody UpdateLabelRequest request) {
        return ResponseEntity.ok(issueLabelService.updateLabel(userId, ownerUsername, repoName, labelName, request));
    }

    @DeleteMapping("/{labelName}")
    public ResponseEntity<Void> deleteLabel(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable String labelName) {
        issueLabelService.deleteLabel(userId, ownerUsername, repoName, labelName);
        return ResponseEntity.noContent().build();
    }
}
