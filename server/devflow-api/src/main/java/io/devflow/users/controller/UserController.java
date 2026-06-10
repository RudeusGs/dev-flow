package io.devflow.users.controller;

import io.devflow.security.CurrentUser;
import io.devflow.security.SecurityUtils;
import io.devflow.users.dto.UserProfileResponse;
import io.devflow.users.dto.UserSummaryDto;
import io.devflow.users.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final SecurityUtils securityUtils;

    public UserController(UserService userService, SecurityUtils securityUtils) {
        this.userService = userService;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable String username) {
        Optional<UUID> currentUserId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(userService.getUserProfile(username, currentUserId));
    }

    @PostMapping("/{username}/follow")
    public ResponseEntity<Void> followUser(@CurrentUser UUID userId, @PathVariable String username) {
        userService.followUser(userId, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{username}/follow")
    public ResponseEntity<Void> unfollowUser(@CurrentUser UUID userId, @PathVariable String username) {
        userService.unfollowUser(userId, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{username}/followers")
    public ResponseEntity<Page<UserSummaryDto>> getFollowers(@PathVariable String username, Pageable pageable) {
        return ResponseEntity.ok(userService.getFollowers(username, pageable));
    }

    @GetMapping("/{username}/following")
    public ResponseEntity<Page<UserSummaryDto>> getFollowing(@PathVariable String username, Pageable pageable) {
        return ResponseEntity.ok(userService.getFollowing(username, pageable));
    }
}
