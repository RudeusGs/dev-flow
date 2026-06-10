package io.devflow.notifications.service;

import io.devflow.common.exception.ResourceNotFoundException;
import io.devflow.notifications.dto.NotificationDto;
import io.devflow.notifications.entity.Notification;
import io.devflow.notifications.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public Page<NotificationDto> getNotifications(UUID userId, boolean unreadOnly, Pageable pageable) {
        Page<Notification> notifications;
        if (unreadOnly) {
            notifications = notificationRepository.findByRecipientIdAndReadFalse(userId, pageable);
        } else {
            notifications = notificationRepository.findByRecipientId(userId, pageable);
        }
        return notifications.map(this::mapToDto);
    }

    @Transactional
    public void markAsRead(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findByIdAndRecipientId(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.markRead();
        notificationRepository.save(notification);
    }

    private NotificationDto mapToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId().toString())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .targetUrl(notification.getTargetUrl())
                .unread(!notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
