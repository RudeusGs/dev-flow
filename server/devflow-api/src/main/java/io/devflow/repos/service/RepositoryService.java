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
    private final io.devflow.repos.repository.RepositoryForkRepository repositoryForkRepository;
    private final GitManagerService gitManagerService;

    public RepositoryService(RepositoryRepository repositoryRepository,
                             UserRepository userRepository,
                             RepositoryStarRepository repositoryStarRepository,
                             RepositoryWatchRepository repositoryWatchRepository,
                             RepositoryPermissionService permissionService,
                             io.devflow.repos.repository.RepositoryForkRepository repositoryForkRepository,
                             GitManagerService gitManagerService) {
        this.repositoryRepository = repositoryRepository;
        this.userRepository = userRepository;
        this.repositoryStarRepository = repositoryStarRepository;
        this.repositoryWatchRepository = repositoryWatchRepository;
        this.permissionService = permissionService;
        this.repositoryForkRepository = repositoryForkRepository;
        this.gitManagerService = gitManagerService;
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

        // Initialize physical bare Git repository
        gitManagerService.initBareRepository(owner.getUsername(), savedRepo.getSlug());

        return mapToResponse(savedRepo, owner.getUsername());
    }

    @Transactional
    public RepositoryResponse updateRepository(UUID userId, String ownerUsername, String repoName, io.devflow.repos.dto.UpdateRepositoryRequest request) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkWritePermission(userId, repo);

        if (request.getName() != null && !request.getName().equalsIgnoreCase(repo.getName())) {
            String newSlug = request.getName().toLowerCase();
            if (repositoryRepository.existsByOwnerIdAndSlug(owner.getId(), newSlug)) {
                throw new DuplicateResourceException("Repository with this name already exists for this owner");
            }
            repo.setName(request.getName());
            repo.setSlug(newSlug);
        }

        if (request.getDescription() != null) {
            repo.setDescription(request.getDescription());
        }

        if (request.getVisibility() != null) {
            repo.setVisibility(request.getVisibility());
        }

        if (request.getDefaultBranch() != null) {
            repo.setDefaultBranchName(request.getDefaultBranch());
        }

        Repository savedRepo = repositoryRepository.save(repo);
        return mapToResponse(savedRepo, ownerUsername);
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
        Page<Repository> repos = repositoryRepository.findByOwnerTypeAndVisibility(RepositoryOwnerType.USER, RepositoryVisibility.PUBLIC, pageable);
        
        java.util.Set<UUID> ownerIds = repos.stream()
                .filter(r -> r.getOwnerType() == RepositoryOwnerType.USER)
                .map(Repository::getOwnerId)
                .collect(java.util.stream.Collectors.toSet());
                
        java.util.Map<UUID, String> ownerUsernames = userRepository.findByIdIn(ownerIds).stream()
                .collect(java.util.stream.Collectors.toMap(User::getId, User::getUsername));
                
        return repos.map(repo -> {
            String ownerUsername = ownerUsernames.getOrDefault(repo.getOwnerId(), "unknown");
            return mapToResponse(repo, ownerUsername);
        });
    }
    
    @Transactional(readOnly = true)
    public Page<RepositoryResponse> listUserRepositories(String username, Optional<UUID> currentUserId, Pageable pageable) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Page<Repository> repos;
        if (currentUserId.isPresent() && owner.getId().equals(currentUserId.get())) {
            repos = repositoryRepository.findByOwnerId(owner.getId(), pageable);
        } else if (currentUserId.isPresent()) {
            repos = repositoryRepository.findUserVisibleRepositories(owner.getId(), currentUserId.get(), pageable);
        } else {
            repos = repositoryRepository.findUserPublicRepositories(owner.getId(), pageable);
        }
        
        return repos.map(repo -> mapToResponse(repo, owner.getUsername()));
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
            
            repositoryRepository.incrementStarsCount(repo.getId());
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
                    repositoryRepository.decrementStarsCount(repo.getId());
                });
    }

    @Transactional
    public void watchRepository(UUID userId, String ownerUsername, String repoName, io.devflow.repos.dto.WatchRepositoryRequest request) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkReadPermission(userId, repo);

        RepositoryWatch watch = repositoryWatchRepository.findByRepositoryIdAndUserId(repo.getId(), userId)
                .orElse(new RepositoryWatch());
                
        watch.setRepositoryId(repo.getId());
        watch.setUserId(userId);
        watch.setNotificationLevel(request.getNotificationLevel());
        
        repositoryWatchRepository.save(watch);
    }

    @Transactional
    public void unwatchRepository(UUID userId, String ownerUsername, String repoName) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        repositoryWatchRepository.findByRepositoryIdAndUserId(repo.getId(), userId)
                .ifPresent(repositoryWatchRepository::delete);
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

        // Optional: rename or delete physical repository
        // gitManagerService.deleteRepository(owner.getUsername(), repo.getSlug());
    }

    @Transactional
    public RepositoryResponse forkRepository(UUID userId, String ownerUsername, String repoName) {
        User sourceOwner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Source owner not found"));

        Repository sourceRepo = repositoryRepository.findByOwnerIdAndSlug(sourceOwner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Source repository not found"));

        permissionService.checkReadPermission(userId, sourceRepo);

        User forker = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String forkSlug = sourceRepo.getName().toLowerCase();
        if (repositoryRepository.existsByOwnerIdAndSlug(userId, forkSlug)) {
            throw new DuplicateResourceException("You already have a repository or fork with this name");
        }

        Repository forkRepo = new Repository();
        forkRepo.setOwnerType(RepositoryOwnerType.USER);
        forkRepo.setOwnerId(userId);
        forkRepo.setName(sourceRepo.getName());
        forkRepo.setSlug(forkSlug);
        forkRepo.setDescription(sourceRepo.getDescription());
        forkRepo.setVisibility(sourceRepo.getVisibility());
        forkRepo.setForkedFromRepositoryId(sourceRepo.getId());

        Repository savedFork = repositoryRepository.save(forkRepo);

        io.devflow.repos.entity.RepositoryFork forkLink = new io.devflow.repos.entity.RepositoryFork();
        forkLink.setSourceRepositoryId(sourceRepo.getId());
        forkLink.setForkRepositoryId(savedFork.getId());
        forkLink.setForkedById(userId);
        repositoryForkRepository.save(forkLink);
        
        repositoryRepository.incrementForksCount(sourceRepo.getId());

        return mapToResponse(savedFork, forker.getUsername());
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
