package io.devflow.notifications.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class NotificationDto {
    private String id;
    private String title;
    private String message;
    private String targetUrl;
    private boolean unread;
    private Instant createdAt;
}
