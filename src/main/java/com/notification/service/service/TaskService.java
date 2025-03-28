package com.notification.service.service;

import com.notification.service.entity.Task;
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
        notificationService.notifyDeveloperMrReviewRequestMessage(task);
        return savedTask;
    }

    public void notifyReviewerTask(Long taskId) {
        Task task = getTaskById(taskId);

        if (task.getStatus() == TaskStatus.OPEN) {
            notificationService.notifyReviewer(task);
        }
    }

    public void notifyThresholdTask(Long taskId) {
        Task task = getTaskById(taskId);

        if (task.getStatus() != TaskStatus.CLOSED) {
            notificationService.notifyReviewerThresholdOnMrRequestMessage(task);
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
        Task taskById = getTaskById(id);
        taskById.setStatus(TaskStatus.CLOSED);
        notificationService.notifyDeveloperMergedMrMessage(taskById);
        return taskRepository.save(taskById);
    }

    public void sendNewFixOnThreshold(Long taskId) {
        Task task = getTaskById(taskId);
        notificationService.notifyDeveloperNewFixOnThresholdRequestMessage(task);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + id + " not found"));
    }

    public Task getTaskByUser(Long userId, UserRole role) {
        if (role == UserRole.DEVELOPER) {
            return taskRepository.findByDeveloperId(userId);
        } else if (role == UserRole.REVIEWER) {
            return taskRepository.findByReviewerId(userId);
        }
        throw new IllegalArgumentException("Unsupported role: " + role);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task updateTaskStatus(long id, TaskStatus status) {
        Task taskById = taskRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Task with id " + id + " not found"));

        taskById.setStatus(status);
        return taskRepository.save(taskById);
    }
}
