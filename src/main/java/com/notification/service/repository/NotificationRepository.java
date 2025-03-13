package com.notification.service.repository;

import com.notification.service.entity.NotificationData;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NotificationRepository extends JpaRepository<NotificationData, Long> {
}
