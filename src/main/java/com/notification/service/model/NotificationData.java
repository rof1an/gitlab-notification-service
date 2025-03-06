package com.notification.service.model;

public record NotificationData(
        String role,
        String title,
        String linkToMr,
        String status,
        Long developerId,
        Long reviewerId
) {
}