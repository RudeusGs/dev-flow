package io.devflow.users.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserProfileResponse {
    private String id;
    private String username;
    private String displayName;
    private String avatarUrl;
    private String bio;
    private String location;
    private String company;
    private String websiteUrl;
    private long followersCount;
    private long followingCount;
    private boolean isFollowing; // true if current user is following this user
}
