package io.devflow.repos.service;

import io.devflow.common.exception.DuplicateResourceException;
import io.devflow.common.exception.ResourceNotFoundException;
import io.devflow.repos.dto.AddCollaboratorRequest;
import io.devflow.repos.dto.RepositoryMemberDto;
import io.devflow.repos.dto.UpdateCollaboratorRoleRequest;
import io.devflow.repos.entity.Repository;
import io.devflow.repos.entity.RepositoryMember;
import io.devflow.repos.repository.RepositoryMemberRepository;
import io.devflow.repos.repository.RepositoryRepository;
import io.devflow.users.entity.User;
import io.devflow.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RepositoryMemberService {

    private final RepositoryMemberRepository memberRepository;
    private final RepositoryRepository repositoryRepository;
    private final UserRepository userRepository;
    private final RepositoryPermissionService permissionService;

    public RepositoryMemberService(RepositoryMemberRepository memberRepository,
                                   RepositoryRepository repositoryRepository,
                                   UserRepository userRepository,
                                   RepositoryPermissionService permissionService) {
        this.memberRepository = memberRepository;
        this.repositoryRepository = repositoryRepository;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
    }

    @Transactional
    public RepositoryMemberDto addCollaborator(UUID currentUserId, String ownerUsername, String repoName, AddCollaboratorRequest request) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        // Only admins can add collaborators
        if (!permissionService.canAdmin(currentUserId, repo)) {
            throw new org.springframework.security.access.AccessDeniedException("You do not have admin access to this repository");
        }

        User targetUser = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User to add not found"));

        if (memberRepository.existsByRepositoryIdAndUserId(repo.getId(), targetUser.getId())) {
            throw new DuplicateResourceException("User is already a collaborator");
        }

        RepositoryMember member = new RepositoryMember();
        member.setRepositoryId(repo.getId());
        member.setUserId(targetUser.getId());
        member.setRole(request.getRole());
        member.setJoinedAt(Instant.now());

        RepositoryMember savedMember = memberRepository.save(member);

        return mapToDto(savedMember, targetUser);
    }

    @Transactional(readOnly = true)
    public List<RepositoryMemberDto> listCollaborators(UUID currentUserId, String ownerUsername, String repoName) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        permissionService.checkReadPermission(currentUserId, repo);

        return memberRepository.findByRepositoryId(repo.getId()).stream()
                .map(member -> {
                    User user = userRepository.findById(member.getUserId()).orElse(null);
                    return mapToDto(member, user);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public RepositoryMemberDto updateCollaborator(UUID currentUserId, String ownerUsername, String repoName, String targetUsername, UpdateCollaboratorRoleRequest request) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        if (!permissionService.canAdmin(currentUserId, repo)) {
            throw new org.springframework.security.access.AccessDeniedException("You do not have admin access to this repository");
        }

        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));

        RepositoryMember member = memberRepository.findByRepositoryIdAndUserId(repo.getId(), targetUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User is not a collaborator"));

        member.setRole(request.getRole());
        
        RepositoryMember savedMember = memberRepository.save(member);
        return mapToDto(savedMember, targetUser);
    }

    @Transactional
    public void removeCollaborator(UUID currentUserId, String ownerUsername, String repoName, String targetUsername) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Repository repo = repositoryRepository.findByOwnerIdAndSlug(owner.getId(), repoName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));

        // Admins can remove anyone. A user can remove themselves.
        if (!permissionService.canAdmin(currentUserId, repo) && !currentUserId.equals(targetUser.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("You do not have permission to remove this collaborator");
        }

        RepositoryMember member = memberRepository.findByRepositoryIdAndUserId(repo.getId(), targetUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User is not a collaborator"));

        memberRepository.delete(member);
    }

    private RepositoryMemberDto mapToDto(RepositoryMember member, User user) {
        return RepositoryMemberDto.builder()
                .id(member.getId().toString())
                .repositoryId(member.getRepositoryId().toString())
                .userId(member.getUserId().toString())
                .username(user != null ? user.getUsername() : "unknown")
                .email(user != null ? user.getEmail() : null)
                .avatarUrl(user != null ? user.getAvatarUrl() : null)
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}
