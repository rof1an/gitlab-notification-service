package com.notification.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification.service.entity.NotificationData;
import com.notification.service.entity.Task;
import com.notification.service.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskLogger {

    private final ObjectMapper objectMapper;

    public void logTask(Task task, UserRole role) {
        NotificationData data = NotificationData.builder()
                .role(role.toString())
                .title(task.getTitle())
                .linkToMr(task.getLinkToMr())
                .status(task.getStatus())
                .developerId(task.getDeveloper().getId())
                .reviewerId(task.getReviewer().getId())
                .build();

        try {
            String writtenValueAsString = objectMapper.writeValueAsString(data);
            System.out.println(writtenValueAsString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сериализации JSON", e);
        }
    }
}
