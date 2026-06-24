package io.devflow.repos.service;

import io.devflow.common.exception.DuplicateResourceException;
import io.devflow.common.exception.ResourceNotFoundException;
import io.devflow.repos.dto.CreateReleaseRequest;
import io.devflow.repos.dto.RepositoryReleaseDto;
import io.devflow.repos.dto.UpdateReleaseRequest;
import io.devflow.repos.entity.Repository;
import io.devflow.repos.entity.RepositoryRelease;
import io.devflow.repos.enums.ReleaseStatus;
import io.devflow.repos.repository.RepositoryReleaseRepository;
import io.devflow.repos.repository.RepositoryRepository;
import io.devflow.users.entity.User;
import io.devflow.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RepositoryReleaseService {

    private final RepositoryReleaseRepository releaseRepository;
    private final RepositoryRepository repositoryRepository;
    private final UserRepository userRepository;
    private final RepositoryPermissionService permissionService;

    public RepositoryReleaseService(RepositoryReleaseRepository releaseRepository,
                                    RepositoryRepository repositoryRepository,
                                    UserRepository userRepository,
                                    RepositoryPermissionService permissionService) {
        this.releaseRepository = releaseRepository;
        this.repositoryRepository = repositoryRepository;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
    }

    @Transactional
    public RepositoryReleaseDto createRelease(UUID userId, String ownerUsername, String repoName, CreateReleaseRequest request) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkWritePermission(userId, repo);

        if (releaseRepository.existsByRepositoryIdAndTagName(repo.getId(), request.getTagName())) {
            throw new DuplicateResourceException("Release with this tag name already exists");
        }

        RepositoryRelease release = new RepositoryRelease();
        release.setRepositoryId(repo.getId());
        release.setTagName(request.getTagName());
        release.setName(request.getName());
        release.setBody(request.getBody());
        release.setPrerelease(request.isPrerelease());
        release.setAuthorId(userId);

        if (request.getStatus() == ReleaseStatus.PUBLISHED) {
            release.publish();
        } else {
            release.setStatus(ReleaseStatus.DRAFT);
        }

        RepositoryRelease savedRelease = releaseRepository.save(release);

        return mapToDtoWithLookup(savedRelease);
    }

    @Transactional(readOnly = true)
    public Page<RepositoryReleaseDto> listReleases(UUID currentUserId, String ownerUsername, String repoName, Pageable pageable) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkReadPermission(currentUserId, repo);

        return releaseRepository.findByRepositoryId(repo.getId(), pageable).map(this::mapToDtoWithLookup);
    }

    @Transactional(readOnly = true)
    public RepositoryReleaseDto getRelease(UUID currentUserId, String ownerUsername, String repoName, String tagName) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkReadPermission(currentUserId, repo);

        RepositoryRelease release = releaseRepository.findByRepositoryIdAndTagName(repo.getId(), tagName)
                .orElseThrow(() -> new ResourceNotFoundException("Release not found"));

        return mapToDtoWithLookup(release);
    }

    @Transactional
    public RepositoryReleaseDto updateRelease(UUID userId, String ownerUsername, String repoName, String tagName, UpdateReleaseRequest request) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkWritePermission(userId, repo);

        RepositoryRelease release = releaseRepository.findByRepositoryIdAndTagName(repo.getId(), tagName)
                .orElseThrow(() -> new ResourceNotFoundException("Release not found"));

        if (request.getName() != null) {
            release.setName(request.getName());
        }

        if (request.getBody() != null) {
            release.setBody(request.getBody());
        }

        if (request.getPrerelease() != null) {
            release.setPrerelease(request.getPrerelease());
        }

        if (request.getStatus() != null && request.getStatus() != release.getStatus()) {
            if (request.getStatus() == ReleaseStatus.PUBLISHED) {
                release.publish();
            } else {
                release.unpublish();
            }
        }

        RepositoryRelease savedRelease = releaseRepository.save(release);

        return mapToDtoWithLookup(savedRelease);
    }

    @Transactional
    public void deleteRelease(UUID userId, String ownerUsername, String repoName, String tagName) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkWritePermission(userId, repo);

        RepositoryRelease release = releaseRepository.findByRepositoryIdAndTagName(repo.getId(), tagName)
                .orElseThrow(() -> new ResourceNotFoundException("Release not found"));

        releaseRepository.delete(release);
    }

    private RepositoryReleaseDto mapToDtoWithLookup(RepositoryRelease release) {
        String authorUsername = "unknown";
        if (release.getAuthorId() != null) {
            authorUsername = userRepository.findById(release.getAuthorId())
                    .map(User::getUsername)
                    .orElse("unknown");
        }

        return RepositoryReleaseDto.builder()
                .id(release.getId().toString())
                .repositoryId(release.getRepositoryId().toString())
                .tagId(release.getTagId() != null ? release.getTagId().toString() : null)
                .tagName(release.getTagName())
                .authorId(release.getAuthorId() != null ? release.getAuthorId().toString() : null)
                .authorUsername(authorUsername)
                .name(release.getName())
                .body(release.getBody())
                .status(release.getStatus())
                .prerelease(release.isPrerelease())
                .publishedAt(release.getPublishedAt())
                .createdAt(release.getCreatedAt())
                .updatedAt(release.getUpdatedAt())
                .build();
    }
}
