package io.devflow.notifications.repository;

import io.devflow.notifications.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByRecipientId(UUID recipientId, Pageable pageable);
    Page<Notification> findByRecipientIdAndReadFalse(UUID recipientId, Pageable pageable);
    Optional<Notification> findByIdAndRecipientId(UUID id, UUID recipientId);
}
