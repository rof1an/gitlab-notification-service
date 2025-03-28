package com.notification.service.handler.config;

import com.notification.service.handler.NotificationHandler;
import com.notification.service.handler.impl.*;
import com.notification.service.model.NotificationType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class NotificationHandlersConfig {

    @Bean
    public Map<NotificationType, NotificationHandler> handlers(
            DeveloperNewMrHandler developerNewMrHandler,
            ReviewerNewMrHandler reviewerNewMrHandler,
            ReviewerThresholdHandler reviewerThresholdHandler,
            DeveloperThresholdFixHandler developerThresholdFixHandler,
            DeveloperMergedMrHandler developerMergedMrHandler
    ) {
        return Map.of(
                NotificationType.SEND_DEVELOPER_NEW_MR_REQUEST_MESSAGE, developerNewMrHandler,
                NotificationType.SEND_REVIEWER_NEW_MR_MESSAGE, reviewerNewMrHandler,
                NotificationType.SEND_REVIEWER_THRESHOLD_REQUEST_MESSAGE, reviewerThresholdHandler,
                NotificationType.SEND_DEVELOPER_THRESHOLD_FIX_REQUEST_MESSAGE, developerThresholdFixHandler,
                NotificationType.SEND_DEVELOPER_MERGED_MR_MESSAGE, developerMergedMrHandler
        );
    }
}
