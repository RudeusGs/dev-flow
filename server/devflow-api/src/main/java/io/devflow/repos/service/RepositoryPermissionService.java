package io.devflow.repos.service;

import io.devflow.common.exception.ResourceNotFoundException;
import io.devflow.repos.entity.Repository;
import io.devflow.repos.entity.RepositoryMember;
import io.devflow.repos.enums.RepositoryMemberRole;
import io.devflow.repos.enums.RepositoryOwnerType;
import io.devflow.repos.enums.RepositoryVisibility;
import io.devflow.repos.repository.RepositoryMemberRepository;
import io.devflow.repos.repository.RepositoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class RepositoryPermissionService {

    private final RepositoryRepository repositoryRepository;
    private final RepositoryMemberRepository repositoryMemberRepository;

    public RepositoryPermissionService(RepositoryRepository repositoryRepository,
                                       RepositoryMemberRepository repositoryMemberRepository) {
        this.repositoryRepository = repositoryRepository;
        this.repositoryMemberRepository = repositoryMemberRepository;
    }

    public boolean canRead(UUID userId, Repository repository) {
        if (repository.getVisibility() == RepositoryVisibility.PUBLIC) {
            return true;
        }
        if (userId == null) {
            return false;
        }
        if (repository.getOwnerType() == RepositoryOwnerType.USER && repository.getOwnerId().equals(userId)) {
            return true;
        }
        return repositoryMemberRepository.existsByRepositoryIdAndUserId(repository.getId(), userId);
    }

    public void checkReadPermission(UUID userId, Repository repository) {
        if (!canRead(userId, repository)) {
            throw new ResourceNotFoundException("Repository not found or you do not have access");
        }
    }

    public boolean canWrite(UUID userId, Repository repository) {
        if (userId == null) {
            return false;
        }
        if (repository.getOwnerType() == RepositoryOwnerType.USER && repository.getOwnerId().equals(userId)) {
            return true;
        }
        
        Optional<RepositoryMember> member = repositoryMemberRepository.findByRepositoryIdAndUserId(repository.getId(), userId);
        if (member.isPresent()) {
            RepositoryMemberRole role = member.get().getRole();
            return role == RepositoryMemberRole.OWNER || role == RepositoryMemberRole.MAINTAINER || role == RepositoryMemberRole.CONTRIBUTOR;
        }
        return false;
    }

    public boolean canAdmin(UUID userId, Repository repository) {
        if (userId == null) {
            return false;
        }
        if (repository.getOwnerType() == RepositoryOwnerType.USER && repository.getOwnerId().equals(userId)) {
            return true;
        }
        
        Optional<RepositoryMember> member = repositoryMemberRepository.findByRepositoryIdAndUserId(repository.getId(), userId);
        return member.map(m -> m.getRole() == RepositoryMemberRole.OWNER || m.getRole() == RepositoryMemberRole.MAINTAINER).orElse(false);
    }
}
