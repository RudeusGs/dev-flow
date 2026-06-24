package io.devflow.repos.dto;

import io.devflow.repos.enums.WatchNotificationLevel;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WatchRepositoryRequest {
    @NotNull(message = "Notification level is required")
    private WatchNotificationLevel notificationLevel;
}
