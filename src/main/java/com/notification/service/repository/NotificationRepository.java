package com.notification.service.repository;

import com.notification.service.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByTaskDeveloperId(Long developerId);

    Optional<Notification> findByTaskReviewerId(Long reviewerId);
}
