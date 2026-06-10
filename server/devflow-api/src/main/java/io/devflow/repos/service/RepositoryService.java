package io.devflow.repos.service;

import io.devflow.common.exception.DuplicateResourceException;
import io.devflow.common.exception.ResourceNotFoundException;
import io.devflow.repos.dto.CreateRepositoryRequest;
import io.devflow.repos.dto.RepositoryResponse;
import io.devflow.repos.entity.Repository;
import io.devflow.repos.entity.RepositoryStar;
import io.devflow.repos.entity.RepositoryWatch;
import io.devflow.repos.enums.RepositoryOwnerType;
import io.devflow.repos.enums.RepositoryVisibility;
import io.devflow.repos.repository.RepositoryRepository;
import io.devflow.repos.repository.RepositoryStarRepository;
import io.devflow.repos.repository.RepositoryWatchRepository;
import io.devflow.users.entity.User;
import io.devflow.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class RepositoryService {

    private final RepositoryRepository repositoryRepository;
    private final UserRepository userRepository;
    private final RepositoryStarRepository repositoryStarRepository;
    private final RepositoryWatchRepository repositoryWatchRepository;
    private final RepositoryPermissionService permissionService;

    public RepositoryService(RepositoryRepository repositoryRepository,
                             UserRepository userRepository,
                             RepositoryStarRepository repositoryStarRepository,
                             RepositoryWatchRepository repositoryWatchRepository,
                             RepositoryPermissionService permissionService) {
        this.repositoryRepository = repositoryRepository;
        this.userRepository = userRepository;
        this.repositoryStarRepository = repositoryStarRepository;
        this.repositoryWatchRepository = repositoryWatchRepository;
        this.permissionService = permissionService;
    }

    @Transactional
    public RepositoryResponse createRepository(UUID ownerId, CreateRepositoryRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner user not found"));

        String slug = request.getName().toLowerCase();
        
        if (repositoryRepository.existsByOwnerIdAndSlug(ownerId, slug)) {
            throw new DuplicateResourceException("Repository with this name already exists for this owner");
        }

        Repository repo = new Repository();
        repo.setOwnerType(RepositoryOwnerType.USER);
        repo.setOwnerId(ownerId);
        repo.setName(request.getName());
        repo.setSlug(slug);
        repo.setDescription(request.getDescription());
        repo.setVisibility(request.getVisibility());
        
        Repository savedRepo = repositoryRepository.save(repo);

        return mapToResponse(savedRepo, owner.getUsername());
    }

    @Transactional(readOnly = true)
    public RepositoryResponse getRepository(String ownerUsername, String repoName, Optional<UUID> currentUserId) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkReadPermission(currentUserId.orElse(null), repo);

        return mapToResponse(repo, owner.getUsername());
    }

    @Transactional(readOnly = true)
    public Page<RepositoryResponse> listPublicRepositories(Pageable pageable) {
        return repositoryRepository.findByOwnerTypeAndVisibility(RepositoryOwnerType.USER, RepositoryVisibility.PUBLIC, pageable)
                .map(this::mapToResponseWithLookup);
    }
    
    @Transactional(readOnly = true)
    public Page<RepositoryResponse> listUserRepositories(String username, Optional<UUID> currentUserId, Pageable pageable) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Page<Repository> repos = repositoryRepository.findByOwnerId(owner.getId(), pageable);
        
        java.util.List<RepositoryResponse> filteredRepos = repos.stream()
                .filter(repo -> permissionService.canRead(currentUserId.orElse(null), repo))
                .map(repo -> mapToResponse(repo, owner.getUsername()))
                .collect(java.util.stream.Collectors.toList());
                
        return new org.springframework.data.domain.PageImpl<>(filteredRepos, pageable, repos.getTotalElements());
    }

    @Transactional
    public void starRepository(UUID userId, String ownerUsername, String repoName) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkReadPermission(userId, repo);

        if (!repositoryStarRepository.existsByRepositoryIdAndUserId(repo.getId(), userId)) {
            RepositoryStar star = new RepositoryStar();
            star.setRepositoryId(repo.getId());
            star.setUserId(userId);
            repositoryStarRepository.save(star);
            
            repo.setStarsCount(repo.getStarsCount() + 1);
            repositoryRepository.save(repo);
        }
    }

    @Transactional
    public void unstarRepository(UUID userId, String ownerUsername, String repoName) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        repositoryStarRepository.findByRepositoryIdAndUserId(repo.getId(), userId)
                .ifPresent(star -> {
                    repositoryStarRepository.delete(star);
                    repo.setStarsCount(repo.getStarsCount() - 1);
                    repositoryRepository.save(repo);
                });
    }

    @Transactional
    public void deleteRepository(UUID userId, String ownerUsername, String repoName) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        if (!permissionService.canAdmin(userId, repo)) {
            throw new org.springframework.security.access.AccessDeniedException("You do not have permission to delete this repository");
        }

        repo.softDelete();
        repositoryRepository.save(repo);
    }

    private RepositoryResponse mapToResponseWithLookup(Repository repo) {
        String ownerUsername = "";
        if (repo.getOwnerType() == RepositoryOwnerType.USER) {
            ownerUsername = userRepository.findById(repo.getOwnerId())
                    .map(User::getUsername)
                    .orElse("unknown");
        }
        return mapToResponse(repo, ownerUsername);
    }

    private RepositoryResponse mapToResponse(Repository repo, String ownerUsername) {
        return RepositoryResponse.builder()
                .id(repo.getId().toString())
                .name(repo.getName())
                .slug(repo.getSlug())
                .description(repo.getDescription())
                .visibility(repo.getVisibility())
                .ownerType(repo.getOwnerType())
                .ownerId(repo.getOwnerId().toString())
                .ownerUsername(ownerUsername)
                .defaultBranchName(repo.getDefaultBranchName())
                .starsCount(repo.getStarsCount())
                .forksCount(repo.getForksCount())
                .issuesCount(repo.getIssuesCount())
                .createdAt(repo.getCreatedAt())
                .updatedAt(repo.getUpdatedAt())
                .build();
    }
}
