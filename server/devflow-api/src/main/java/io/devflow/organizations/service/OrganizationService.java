package io.devflow.organizations.service;

import io.devflow.common.exception.DuplicateResourceException;
import io.devflow.common.exception.ResourceNotFoundException;
import io.devflow.organizations.dto.CreateOrganizationRequest;
import io.devflow.organizations.dto.OrganizationDto;
import io.devflow.organizations.entity.Organization;
import io.devflow.organizations.entity.OrganizationMember;
import io.devflow.organizations.enums.OrganizationMemberRole;
import io.devflow.organizations.repository.OrganizationMemberRepository;
import io.devflow.organizations.repository.OrganizationRepository;
import io.devflow.users.entity.User;
import io.devflow.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final UserRepository userRepository;

    public OrganizationService(OrganizationRepository organizationRepository,
                               OrganizationMemberRepository organizationMemberRepository,
                               UserRepository userRepository) {
        this.organizationRepository = organizationRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrganizationDto createOrganization(UUID userId, CreateOrganizationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (organizationRepository.existsBySlug(request.getName().toLowerCase())) {
            throw new DuplicateResourceException("Organization name already taken");
        }

        Organization org = new Organization();
        org.setSlug(request.getName().toLowerCase());
        org.setDisplayName(request.getDisplayName());
        org.setDescription(request.getDescription());
        org.setBillingEmail(request.getEmail());

        Organization savedOrg = organizationRepository.save(org);

        // Add creator as owner
        OrganizationMember member = new OrganizationMember();
        member.setOrganizationId(savedOrg.getId());
        member.setUserId(userId);
        member.setRole(OrganizationMemberRole.OWNER);
        organizationMemberRepository.save(member);

        return mapToDto(savedOrg);
    }

    @Transactional(readOnly = true)
    public OrganizationDto getOrganization(String slug) {
        Organization org = organizationRepository.findBySlug(slug.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        return mapToDto(org);
    }

    private OrganizationDto mapToDto(Organization org) {
        return OrganizationDto.builder()
                .id(org.getId().toString())
                .name(org.getSlug())
                .displayName(org.getDisplayName())
                .description(org.getDescription())
                .email(org.getBillingEmail())
                .avatarUrl(org.getAvatarUrl())
                .location(org.getLocation())
                .websiteUrl(org.getWebsiteUrl())
                .build();
    }
}
