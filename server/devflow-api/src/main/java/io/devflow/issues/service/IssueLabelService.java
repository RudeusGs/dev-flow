package io.devflow.issues.service;

import io.devflow.common.exception.DuplicateResourceException;
import io.devflow.common.exception.ResourceNotFoundException;
import io.devflow.issues.dto.CreateLabelRequest;
import io.devflow.issues.dto.IssueLabelDto;
import io.devflow.issues.dto.UpdateLabelRequest;
import io.devflow.issues.entity.IssueLabel;
import io.devflow.issues.repository.IssueLabelRepository;
import io.devflow.repos.entity.Repository;
import io.devflow.repos.repository.RepositoryRepository;
import io.devflow.repos.service.RepositoryPermissionService;
import io.devflow.users.entity.User;
import io.devflow.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class IssueLabelService {

    private final IssueLabelRepository issueLabelRepository;
    private final RepositoryRepository repositoryRepository;
    private final UserRepository userRepository;
    private final RepositoryPermissionService permissionService;

    public IssueLabelService(IssueLabelRepository issueLabelRepository,
                             RepositoryRepository repositoryRepository,
                             UserRepository userRepository,
                             RepositoryPermissionService permissionService) {
        this.issueLabelRepository = issueLabelRepository;
        this.repositoryRepository = repositoryRepository;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
    }

    @Transactional
    public IssueLabelDto createLabel(UUID userId, String ownerUsername, String repoName, CreateLabelRequest request) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkWritePermission(userId, repo);

        if (issueLabelRepository.existsByRepositoryIdAndNameIgnoreCase(repo.getId(), request.getName())) {
            throw new DuplicateResourceException("Label with this name already exists in the repository");
        }

        IssueLabel label = new IssueLabel();
        label.setRepositoryId(repo.getId());
        label.setName(request.getName());
        label.setColor(request.getColor());
        label.setDescription(request.getDescription());
        IssueLabel savedLabel = issueLabelRepository.save(label);

        return mapToDto(savedLabel);
    }

    @Transactional(readOnly = true)
    public Page<IssueLabelDto> listLabels(UUID currentUserId, String ownerUsername, String repoName, Pageable pageable) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkReadPermission(currentUserId, repo);

        return issueLabelRepository.findByRepositoryId(repo.getId(), pageable).map(this::mapToDto);
    }

    @Transactional
    public IssueLabelDto updateLabel(UUID userId, String ownerUsername, String repoName, String labelName, UpdateLabelRequest request) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkWritePermission(userId, repo);

        IssueLabel label = issueLabelRepository.findByRepositoryIdAndNameIgnoreCase(repo.getId(), labelName)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));

        if (request.getName() != null && !request.getName().equalsIgnoreCase(label.getName())) {
            if (issueLabelRepository.existsByRepositoryIdAndNameIgnoreCase(repo.getId(), request.getName())) {
                throw new DuplicateResourceException("Label with this name already exists in the repository");
            }
            label.setName(request.getName());
        }

        if (request.getColor() != null) {
            label.setColor(request.getColor());
        }

        if (request.getDescription() != null) {
            label.setDescription(request.getDescription());
        }

        IssueLabel savedLabel = issueLabelRepository.save(label);

        return mapToDto(savedLabel);
    }

    @Transactional
    public void deleteLabel(UUID userId, String ownerUsername, String repoName, String labelName) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkWritePermission(userId, repo);

        IssueLabel label = issueLabelRepository.findByRepositoryIdAndNameIgnoreCase(repo.getId(), labelName)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));

        issueLabelRepository.delete(label);
    }

    private IssueLabelDto mapToDto(IssueLabel label) {
        return IssueLabelDto.builder()
                .id(label.getId().toString())
                .repositoryId(label.getRepositoryId().toString())
                .name(label.getName())
                .color(label.getColor())
                .description(label.getDescription())
                .createdAt(label.getCreatedAt())
                .build();
    }
}
