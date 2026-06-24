package io.devflow.repos.service;

import io.devflow.common.exception.DuplicateResourceException;
import io.devflow.common.exception.ResourceNotFoundException;
import io.devflow.repos.dto.CreateTagRequest;
import io.devflow.repos.dto.RepositoryTagDto;
import io.devflow.repos.entity.Repository;
import io.devflow.repos.entity.RepositoryTag;
import io.devflow.repos.repository.RepositoryRepository;
import io.devflow.repos.repository.RepositoryTagRepository;
import io.devflow.users.entity.User;
import io.devflow.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RepositoryTagService {

    private final RepositoryTagRepository tagRepository;
    private final RepositoryRepository repositoryRepository;
    private final UserRepository userRepository;
    private final RepositoryPermissionService permissionService;

    public RepositoryTagService(RepositoryTagRepository tagRepository,
                                RepositoryRepository repositoryRepository,
                                UserRepository userRepository,
                                RepositoryPermissionService permissionService) {
        this.tagRepository = tagRepository;
        this.repositoryRepository = repositoryRepository;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
    }

    @Transactional
    public RepositoryTagDto createTag(UUID userId, String ownerUsername, String repoName, CreateTagRequest request) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkWritePermission(userId, repo);

        if (tagRepository.existsByRepositoryIdAndName(repo.getId(), request.getName())) {
            throw new DuplicateResourceException("Tag with this name already exists");
        }

        RepositoryTag tag = new RepositoryTag();
        tag.setRepositoryId(repo.getId());
        tag.setName(request.getName());
        tag.setCommitId(UUID.fromString(request.getCommitId()));
        tag.setTaggedById(userId);
        tag.setMessage(request.getMessage());

        RepositoryTag savedTag = tagRepository.save(tag);

        return mapToDtoWithLookup(savedTag);
    }

    @Transactional(readOnly = true)
    public Page<RepositoryTagDto> listTags(UUID currentUserId, String ownerUsername, String repoName, Pageable pageable) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkReadPermission(currentUserId, repo);

        return tagRepository.findByRepositoryId(repo.getId(), pageable).map(this::mapToDtoWithLookup);
    }

    @Transactional(readOnly = true)
    public RepositoryTagDto getTag(UUID currentUserId, String ownerUsername, String repoName, String tagName) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkReadPermission(currentUserId, repo);

        RepositoryTag tag = tagRepository.findByRepositoryIdAndName(repo.getId(), tagName)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));

        return mapToDtoWithLookup(tag);
    }

    @Transactional
    public void deleteTag(UUID userId, String ownerUsername, String repoName, String tagName) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkWritePermission(userId, repo);

        RepositoryTag tag = tagRepository.findByRepositoryIdAndName(repo.getId(), tagName)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));

        tagRepository.delete(tag);
    }

    private RepositoryTagDto mapToDtoWithLookup(RepositoryTag tag) {
        String taggedByUsername = "unknown";
        if (tag.getTaggedById() != null) {
            taggedByUsername = userRepository.findById(tag.getTaggedById())
                    .map(User::getUsername)
                    .orElse("unknown");
        }

        return RepositoryTagDto.builder()
                .id(tag.getId().toString())
                .repositoryId(tag.getRepositoryId().toString())
                .name(tag.getName())
                .commitId(tag.getCommitId() != null ? tag.getCommitId().toString() : null)
                .taggedById(tag.getTaggedById() != null ? tag.getTaggedById().toString() : null)
                .taggedByUsername(taggedByUsername)
                .message(tag.getMessage())
                .createdAt(tag.getCreatedAt())
                .build();
    }
}
