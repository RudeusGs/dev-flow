package io.devflow.branches.controller;

import io.devflow.branches.dto.BranchDto;
import io.devflow.branches.dto.CreateBranchRequest;
import io.devflow.branches.service.BranchService;
import io.devflow.security.CurrentUser;
import io.devflow.security.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/repos/{ownerUsername}/{repoName}/branches")
public class BranchController {

    private final BranchService branchService;
    private final SecurityUtils securityUtils;

    public BranchController(BranchService branchService, SecurityUtils securityUtils) {
        this.branchService = branchService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<List<BranchDto>> listBranches(
            @PathVariable String ownerUsername,
            @PathVariable String repoName) {
        UUID currentUserId = securityUtils.getCurrentUserId().orElse(null);
        return ResponseEntity.ok(branchService.listBranches(currentUserId, ownerUsername, repoName));
    }

    @PostMapping
    public ResponseEntity<BranchDto> createBranch(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @Valid @RequestBody CreateBranchRequest request) {
        BranchDto branchDto = branchService.createBranch(userId, ownerUsername, repoName, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(branchDto);
    }

    @DeleteMapping("/{branchName}")
    public ResponseEntity<Void> deleteBranch(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable String branchName) {
        branchService.deleteBranch(userId, ownerUsername, repoName, branchName);
        return ResponseEntity.noContent().build();
    }
}
