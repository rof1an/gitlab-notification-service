package com.notification.service.service;

import com.notification.service.entity.Notification;
import com.notification.service.entity.Task;
import com.notification.service.model.NotificationType;
import com.notification.service.util.TelegramMessageFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationFactory {

    private final TelegramMessageFormatter messageFormatter;

    public Notification createNotification(Task task, NotificationType type) {
        return Notification.builder()
                .notificationType(type)
                .message(getFormattedMessage(task, type))
                .task(task)
                .build();
    }

    private String getFormattedMessage(Task task, NotificationType type) {
        return switch (type) {
            case SEND_DEVELOPER_NEW_MR_REQUEST_MESSAGE -> messageFormatter.formatDeveloperMrReviewRequestMessage(task);
            case SEND_REVIEWER_NEW_MR_MESSAGE -> messageFormatter.formatReviewerNewMrMessage(task);
            case SEND_DEVELOPER_MERGED_MR_MESSAGE -> messageFormatter.formatDeveloperMergedMrMessage(task);
            case SEND_REVIEWER_THRESHOLD_REQUEST_MESSAGE -> messageFormatter.formatReviewerThresholdOnMrRequestMessage(task);
            case SEND_DEVELOPER_THRESHOLD_FIX_REQUEST_MESSAGE -> messageFormatter.formatDeveloperNewFixOnThresholdRequestMessage(task);
            case REMIND_REVIEWER_UNCHECKED_MR -> messageFormatter.formatActiveMergeRequestRemind(task);
        };
    }
}
