package com.notification.service.repository;

import com.notification.service.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByTaskDeveloperId(Long developerId);

    Optional<Notification> findByTaskReviewerId(Long reviewerId);
}
