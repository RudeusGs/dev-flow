package io.devflow.organizations.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrganizationDto {
    private String id;
    private String name;
    private String displayName;
    private String description;
    private String email;
    private String avatarUrl;
    private String location;
    private String websiteUrl;
}
