package io.devflow.repos.controller;

import io.devflow.repos.dto.AddCollaboratorRequest;
import io.devflow.repos.dto.RepositoryMemberDto;
import io.devflow.repos.dto.UpdateCollaboratorRoleRequest;
import io.devflow.repos.service.RepositoryMemberService;
import io.devflow.security.CurrentUser;
import io.devflow.security.SecurityUtils;
import jakarta.validation.Valid;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/repos/{ownerUsername}/{repoName}/collaborators")
public class RepositoryMemberController {

    private final RepositoryMemberService memberService;
    private final SecurityUtils securityUtils;

    public RepositoryMemberController(RepositoryMemberService memberService, SecurityUtils securityUtils) {
        this.memberService = memberService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<RepositoryMemberDto> addCollaborator(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @Valid @RequestBody AddCollaboratorRequest request) {
        RepositoryMemberDto dto = memberService.addCollaborator(userId, ownerUsername, repoName, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<RepositoryMemberDto>> listCollaborators(
            @PathVariable String ownerUsername,
            @PathVariable String repoName) {
        UUID currentUserId = securityUtils.getCurrentUserId().orElse(null);
        return ResponseEntity.ok(memberService.listCollaborators(currentUserId, ownerUsername, repoName));
    }

    @PatchMapping("/{targetUsername}")
    public ResponseEntity<RepositoryMemberDto> updateCollaboratorRole(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable String targetUsername,
            @Valid @RequestBody UpdateCollaboratorRoleRequest request) {
        return ResponseEntity.ok(memberService.updateCollaborator(userId, ownerUsername, repoName, targetUsername, request));
    }

    @DeleteMapping("/{targetUsername}")
    public ResponseEntity<Void> removeCollaborator(
            @CurrentUser UUID userId,
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable String targetUsername) {
        memberService.removeCollaborator(userId, ownerUsername, repoName, targetUsername);
        return ResponseEntity.noContent().build();
    }
}
