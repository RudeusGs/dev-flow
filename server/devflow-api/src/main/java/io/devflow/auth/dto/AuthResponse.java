package io.devflow.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private UserDto user;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserDto {
        private String id;
        private String username;
        private String email;
        private String displayName;
        private String avatarUrl;
    }
}
