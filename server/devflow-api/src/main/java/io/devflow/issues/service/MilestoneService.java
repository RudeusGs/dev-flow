package io.devflow.issues.service;

import io.devflow.common.exception.DuplicateResourceException;
import io.devflow.common.exception.ResourceNotFoundException;
import io.devflow.issues.dto.CreateMilestoneRequest;
import io.devflow.issues.dto.MilestoneDto;
import io.devflow.issues.dto.UpdateMilestoneRequest;
import io.devflow.issues.entity.Milestone;
import io.devflow.issues.enums.MilestoneStatus;
import io.devflow.issues.repository.MilestoneRepository;
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
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final RepositoryRepository repositoryRepository;
    private final UserRepository userRepository;
    private final RepositoryPermissionService permissionService;

    public MilestoneService(MilestoneRepository milestoneRepository,
                            RepositoryRepository repositoryRepository,
                            UserRepository userRepository,
                            RepositoryPermissionService permissionService) {
        this.milestoneRepository = milestoneRepository;
        this.repositoryRepository = repositoryRepository;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
    }

    @Transactional
    public MilestoneDto createMilestone(UUID userId, String ownerUsername, String repoName, CreateMilestoneRequest request) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkWritePermission(userId, repo);

        if (milestoneRepository.existsByRepositoryIdAndTitleIgnoreCase(repo.getId(), request.getTitle())) {
            throw new DuplicateResourceException("Milestone with this title already exists in the repository");
        }

        Milestone milestone = new Milestone();
        milestone.setRepositoryId(repo.getId());
        milestone.setTitle(request.getTitle());
        milestone.setDescription(request.getDescription());
        milestone.setDueAt(request.getDueAt());
        Milestone savedMilestone = milestoneRepository.save(milestone);

        return mapToDto(savedMilestone);
    }

    @Transactional(readOnly = true)
    public Page<MilestoneDto> listMilestones(UUID currentUserId, String ownerUsername, String repoName, MilestoneStatus status, Pageable pageable) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkReadPermission(currentUserId, repo);

        if (status != null) {
            return milestoneRepository.findByRepositoryIdAndStatus(repo.getId(), status, pageable).map(this::mapToDto);
        }
        return milestoneRepository.findByRepositoryId(repo.getId(), pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public MilestoneDto getMilestone(UUID currentUserId, String ownerUsername, String repoName, String title) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkReadPermission(currentUserId, repo);

        Milestone milestone = milestoneRepository.findByRepositoryIdAndTitleIgnoreCase(repo.getId(), title)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone not found"));

        return mapToDto(milestone);
    }

    @Transactional
    public MilestoneDto updateMilestone(UUID userId, String ownerUsername, String repoName, String title, UpdateMilestoneRequest request) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkWritePermission(userId, repo);

        Milestone milestone = milestoneRepository.findByRepositoryIdAndTitleIgnoreCase(repo.getId(), title)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone not found"));

        if (request.getTitle() != null && !request.getTitle().equalsIgnoreCase(milestone.getTitle())) {
            if (milestoneRepository.existsByRepositoryIdAndTitleIgnoreCase(repo.getId(), request.getTitle())) {
                throw new DuplicateResourceException("Milestone with this title already exists in the repository");
            }
            milestone.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            milestone.setDescription(request.getDescription());
        }

        if (request.getDueAt() != null) {
            milestone.setDueAt(request.getDueAt());
        }

        if (request.getStatus() != null && request.getStatus() != milestone.getStatus()) {
            if (request.getStatus() == MilestoneStatus.CLOSED) {
                milestone.close();
            } else {
                milestone.reopen();
            }
        }

        Milestone savedMilestone = milestoneRepository.save(milestone);

        return mapToDto(savedMilestone);
    }

    @Transactional
    public void deleteMilestone(UUID userId, String ownerUsername, String repoName, String title) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkWritePermission(userId, repo);

        Milestone milestone = milestoneRepository.findByRepositoryIdAndTitleIgnoreCase(repo.getId(), title)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone not found"));

        milestoneRepository.delete(milestone);
    }

    private MilestoneDto mapToDto(Milestone milestone) {
        return MilestoneDto.builder()
                .id(milestone.getId().toString())
                .repositoryId(milestone.getRepositoryId().toString())
                .title(milestone.getTitle())
                .description(milestone.getDescription())
                .status(milestone.getStatus())
                .dueAt(milestone.getDueAt())
                .closedAt(milestone.getClosedAt())
                .createdAt(milestone.getCreatedAt())
                .updatedAt(milestone.getUpdatedAt())
                .build();
    }
}
