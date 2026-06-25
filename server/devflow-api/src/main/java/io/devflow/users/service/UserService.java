package io.devflow.users.service;

import io.devflow.common.exception.ResourceNotFoundException;
import io.devflow.users.dto.UserProfileResponse;
import io.devflow.users.dto.UserSummaryDto;
import io.devflow.users.entity.User;
import io.devflow.users.entity.UserFollow;
import io.devflow.users.repository.UserFollowRepository;
import io.devflow.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;

    public UserService(UserRepository userRepository, UserFollowRepository userFollowRepository) {
        this.userRepository = userRepository;
        this.userFollowRepository = userFollowRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String username, Optional<UUID> currentUserId) {
        User user = userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        long followersCount = userFollowRepository.countByFollowingId(user.getId());
        long followingCount = userFollowRepository.countByFollowerId(user.getId());

        boolean isFollowing = false;
        if (currentUserId.isPresent() && !currentUserId.get().equals(user.getId())) {
            isFollowing = userFollowRepository.existsByFollowerIdAndFollowingId(currentUserId.get(), user.getId());
        }

        return UserProfileResponse.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .location(user.getLocation())
                .company(user.getCompany())
                .websiteUrl(user.getWebsiteUrl())
                .followersCount(followersCount)
                .followingCount(followingCount)
                .isFollowing(isFollowing)
                .build();
    }

    @Transactional
    public void followUser(UUID currentUserId, String targetUsername) {
        User targetUser = userRepository.findByUsername(targetUsername.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + targetUsername));

        if (currentUserId.equals(targetUser.getId())) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        if (!userFollowRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUser.getId())) {
            UserFollow follow = new UserFollow();
            follow.setFollowerId(currentUserId);
            follow.setFollowingId(targetUser.getId());
            userFollowRepository.save(follow);
        }
    }

    @Transactional
    public void unfollowUser(UUID currentUserId, String targetUsername) {
        User targetUser = userRepository.findByUsername(targetUsername.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + targetUsername));

        userFollowRepository.findByFollowerIdAndFollowingId(currentUserId, targetUser.getId())
                .ifPresent(userFollowRepository::delete);
    }

    @Transactional(readOnly = true)
    public Page<UserSummaryDto> getFollowers(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Page<UserFollow> follows = userFollowRepository.findByFollowingId(user.getId(), pageable);
        List<UUID> followerIds = follows.stream().map(UserFollow::getFollowerId).collect(Collectors.toList());
        Map<UUID, User> usersMap = userRepository.findAllById(followerIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        return follows.map(follow -> mapToSummaryDto(usersMap.get(follow.getFollowerId())));
    }

    @Transactional(readOnly = true)
    public Page<UserSummaryDto> getFollowing(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Page<UserFollow> follows = userFollowRepository.findByFollowerId(user.getId(), pageable);
        List<UUID> followingIds = follows.stream().map(UserFollow::getFollowingId).collect(Collectors.toList());
        Map<UUID, User> usersMap = userRepository.findAllById(followingIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        return follows.map(follow -> mapToSummaryDto(usersMap.get(follow.getFollowingId())));
    }

    private UserSummaryDto mapToSummaryDto(User u) {
        if (u == null) return new UserSummaryDto();
        return new UserSummaryDto(u.getId().toString(), u.getUsername(), u.getDisplayName(), u.getAvatarUrl());
    }
}
