package com.notification.service.core.service;

import com.notification.service.core.entity.Task;
import com.notification.service.core.model.NotificationType;
import com.notification.service.core.model.TaskStatus;
import com.notification.service.core.model.UserRole;
import com.notification.service.core.repository.TaskRepository;
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
        notificationService.createNotification(task, NotificationType.SEND_DEVELOPER_NEW_MR_REQUEST_MESSAGE);
        return savedTask;
    }

    public void notifyReviewerTask(Long taskId) {
        Task task = getTaskById(taskId);

        if (task.getStatus() == TaskStatus.OPEN) {
            notificationService.createNotification(task, NotificationType.SEND_REVIEWER_NEW_MR_MESSAGE);
        }
    }

    public Task notifyThresholdTask(Long taskId) {
        Task task = getTaskById(taskId);

        if (task.getStatus() != TaskStatus.CLOSED) {
            notificationService.createNotification(task, NotificationType.SEND_REVIEWER_THRESHOLD_REQUEST_MESSAGE);
        }
        return task;
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

        notificationService.createNotification(task, NotificationType.SEND_DEVELOPER_MERGED_MR_MESSAGE);
        return taskRepository.save(task);
    }

    public Task sendNewFixOnThreshold(Long taskId) {
        Task task = getTaskById(taskId);
        notificationService.createNotification(task, NotificationType.SEND_DEVELOPER_THRESHOLD_FIX_REQUEST_MESSAGE);
        return task;
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + id + " not found"));
    }

    public List<Task> getTasksByStatuses(List<TaskStatus> taskStatuses){
        return taskRepository.findAllByStatusIn(taskStatuses);
    }

    public Task getTaskByUser(Long userId, UserRole role) {
        if (role == UserRole.DEVELOPER) {
            return taskRepository.findByDeveloperId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Developer not found with id = " + userId));
        } else if (role == UserRole.REVIEWER) {
            return taskRepository.findByReviewerId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Reviewer not found with id = " + userId));
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
