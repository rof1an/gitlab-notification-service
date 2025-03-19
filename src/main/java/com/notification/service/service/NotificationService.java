package com.notification.service.service;

import com.notification.service.entity.Notification;
import com.notification.service.entity.Task;
import com.notification.service.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void saveNotification(String message, Notification notification) {
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    public List<Notification> getNotifications() {
        return notificationRepository.findAll();
    }

    public void setNotificationRed(Long notificationId, Boolean isRead) {
        Notification notificationById = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification with id " + notificationId + " not found"));

        notificationById.setRead(true);
        notificationRepository.save(notificationById);
    }

    public void notifyDeveloper(Task task) {
        String message = String.format("Новая задача для разработчика: %s\nСсылка: %s",
                task.getTitle(), task.getLinkToMr());

        long developerId = task.getDeveloper().getId();
        saveNotification(message, getOrCreateNotificationByDeveloperId(task, message));
    }

    public void notifyReviewer(Task task) {
        String message = String.format("Новая задача на ревью: %s\nСсылка: %s",
                task.getTitle(), task.getLinkToMr());

        saveNotification(message, getOrCreateNotificationByReviewerId(task));
    }

    public void notifyThresholdReviewer(Task task) {
        String message = "Threshold отслежен. Подтвердите отправку чтобы отправить разработчику";

        saveNotification(message, getOrCreateNotificationByReviewerId(task));
    }

    public void acceptThresholdTaskNotify(Task task) {
        String message = "Ревьюер подтведил threshold, уведомляем разработчика...";

        saveNotification(message, getOrCreateNotificationByDeveloperId(task, message));
    }

    public Notification getNotificationByDeveloperId(Long developerId) {
        return notificationRepository.findByTaskDeveloperId(developerId)
                .orElseThrow(() -> new EntityNotFoundException("Notification with id " + developerId + " not found"));
    }

    public Notification getOrCreateNotificationByDeveloperId(Task task, String notificationMessage) {
        return notificationRepository.findByTaskDeveloperId(task.getDeveloper().getId())
                .orElseGet(() -> {
                    Notification newNotification = new Notification();
                    newNotification.setMessage(notificationMessage);
                    newNotification.setTask(task);
                    return notificationRepository.save(newNotification);
                });
    }

    public Notification getOrCreateNotificationByReviewerId(Task task) {
        return notificationRepository.findByTaskReviewerId(task.getReviewer().getId())
                .orElseGet(() -> {
                    Notification newNotification = new Notification();
                    newNotification.setTask(task);
                    return notificationRepository.save(newNotification);
                });
    }
}
