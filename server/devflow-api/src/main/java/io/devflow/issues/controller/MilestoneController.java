package io.devflow.issues.controller;

import io.devflow.issues.dto.CreateMilestoneRequest;
import io.devflow.issues.dto.MilestoneDto;
import io.devflow.issues.dto.UpdateMilestoneRequest;
import io.devflow.issues.enums.MilestoneStatus;
import io.devflow.issues.service.MilestoneService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/repos/{ownerUsername}/{repoName}/milestones")
public class MilestoneController {

    private final MilestoneService milestoneService;
    private final SecurityUtils securityUtils;

    public MilestoneController(MilestoneService milestoneService, SecurityUtils securityUtils) {
        this.milestoneService = milestoneService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<MilestoneDto> createMilestone(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @Valid @RequestBody CreateMilestoneRequest request) {
        MilestoneDto dto = milestoneService.createMilestone(userId, ownerUsername, repoName, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<Page<MilestoneDto>> listMilestones(
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @RequestParam(required = false) MilestoneStatus status,
            Pageable pageable) {
        UUID currentUserId = securityUtils.getCurrentUserId().orElse(null);
        return ResponseEntity.ok(milestoneService.listMilestones(currentUserId, ownerUsername, repoName, status, pageable));
    }

    @GetMapping("/{title}")
    public ResponseEntity<MilestoneDto> getMilestone(
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable String title) {
        UUID currentUserId = securityUtils.getCurrentUserId().orElse(null);
        return ResponseEntity.ok(milestoneService.getMilestone(currentUserId, ownerUsername, repoName, title));
    }

    @PatchMapping("/{title}")
    public ResponseEntity<MilestoneDto> updateMilestone(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable String title,
            @Valid @RequestBody UpdateMilestoneRequest request) {
        return ResponseEntity.ok(milestoneService.updateMilestone(userId, ownerUsername, repoName, title, request));
    }

    @DeleteMapping("/{title}")
    public ResponseEntity<Void> deleteMilestone(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable String title) {
        milestoneService.deleteMilestone(userId, ownerUsername, repoName, title);
        return ResponseEntity.noContent().build();
    }
}
