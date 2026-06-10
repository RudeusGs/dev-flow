package io.devflow.sourcefiles.controller;

import io.devflow.security.SecurityUtils;
import io.devflow.sourcefiles.dto.SourceFileDto;
import io.devflow.sourcefiles.service.SourceFileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/repos/{ownerUsername}/{repoName}/branches/{branchName}/files")
public class SourceFileController {

    private final SourceFileService sourceFileService;
    private final SecurityUtils securityUtils;

    public SourceFileController(SourceFileService sourceFileService, SecurityUtils securityUtils) {
        this.sourceFileService = sourceFileService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<List<SourceFileDto>> listFiles(
            @PathVariable String ownerUsername,
            @PathVariable String repoName,
            @PathVariable String branchName,
            @RequestParam(required = false) String path) {
        UUID currentUserId = securityUtils.getCurrentUserId().orElse(null);
        return ResponseEntity.ok(sourceFileService.listFiles(currentUserId, ownerUsername, repoName, branchName, path));
    }
}
