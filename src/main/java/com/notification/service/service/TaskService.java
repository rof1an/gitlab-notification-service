package com.notification.service.service;

import com.notification.service.entity.Task;
import com.notification.service.model.NotificationType;
import com.notification.service.model.TaskStatus;
import com.notification.service.model.UserRole;
import com.notification.service.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final NotificationService notificationService;

    public Task createTask(Task task) {
        task.setStatus(TaskStatus.OPEN);
        Task savedTask = taskRepository.save(task);
        notificationService.notify(task, NotificationType.SEND_DEVELOPER_NEW_MR_REQUEST_MESSAGE);
        return savedTask;
    }

    public void notifyReviewerTask(Long taskId) {
        Task task = getTaskById(taskId);

        if (task.getStatus() == TaskStatus.OPEN) {
            notificationService.notify(task, NotificationType.SEND_REVIEWER_NEW_MR_MESSAGE);
        }
    }

    public void notifyThresholdTask(Long taskId) {
        Task task = getTaskById(taskId);

        if (task.getStatus() != TaskStatus.CLOSED) {
            notificationService.notify(task, NotificationType.SEND_REVIEWER_THRESHOLD_REQUEST_MESSAGE);
        }
    }

    public void confirmThresholdTaskNotify(Long taskId) {
        Task task = getTaskById(taskId);

        if (task.getStatus() != TaskStatus.CLOSED) {
            task.setStatus(TaskStatus.NEED_FIXES);
            notificationService.confirmThresholdTaskNotifyToDeveloper(task);
        }
    }

    public Task mergeTask(Long id) {
        Task task = getTaskById(id);
        task.setStatus(TaskStatus.CLOSED);

        notificationService.notify(task, NotificationType.SEND_DEVELOPER_MERGED_MR_MESSAGE);
        return taskRepository.save(task);
    }

    public void sendNewFixOnThreshold(Long taskId) {
        Task task = getTaskById(taskId);
        notificationService.notify(task, NotificationType.SEND_DEVELOPER_THRESHOLD_FIX_REQUEST_MESSAGE);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + id + " not found"));
    }

    public Task getTaskByUser(Long userId, UserRole role) {
        if (role == UserRole.DEVELOPER) {
            return taskRepository.findByDeveloperId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Developer not found with id = " + userId));
        } else if (role == UserRole.REVIEWER) {
            return taskRepository.findByReviewerId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Developer not found with id = " + userId));
        }
        throw new IllegalArgumentException("Unsupported role: " + role);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskByLink(String link) {
        return taskRepository.findTaskByLinkToMr(link)
                .orElseThrow(() -> new EntityNotFoundException("Merge Request not found with link - " + link));
    }

    public Task updateTaskStatus(long id, TaskStatus status) {
        Task taskById = taskRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Task with id " + id + " not found"));

        taskById.setStatus(status);
        return taskRepository.save(taskById);
    }
}
