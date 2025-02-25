package com.notification.service.service;

import com.notification.service.entity.Task;
import com.notification.service.model.TaskStatus;
import com.notification.service.model.UserRole;
import com.notification.service.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final NotificationService notificationService;

    public Task createTask(Task task) {
        task.setStatus(TaskStatus.OPEN);
        notificationService.notifyDeveloper(task);
        return taskRepository.save(task);
    }

    public Task mergedTask(Long id) {
        Task taskById = getTaskById(id);
        taskById.setStatus(TaskStatus.CLOSED);
        return taskRepository.save(taskById);
    }

    public List<Task> getTasksByUser(Long id, UserRole role) {
        if (Optional.ofNullable(role).isPresent()) {
            if (role.equals(UserRole.DEVELOPER)) {
                List<Task> tasks = taskRepository.findAllByDeveloperId(id);
                tasks.forEach(task -> notificationService.logTasks(task, UserRole.DEVELOPER));

                return tasks;
            }

            if (role.equals(UserRole.REVIEWER)) {
                List<Task> tasks = taskRepository.findAllByReviewerId(id);
                tasks.forEach(task -> notificationService.logTasks(task, UserRole.REVIEWER));

                return tasks;
            }
        }

        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + id + " not found"));
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

    public void notifyDeveloperTask(Long taskId) {
        Task newTaskById = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + taskId + " not found"));

        if (newTaskById.getStatus() != TaskStatus.CLOSED) {
            notificationService.notifyDeveloper(newTaskById);
        }
    }

    public void notifyReviewerTask(Long taskId) {
        Task newTaskById = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + taskId + " not found"));

        if (newTaskById.getStatus() != TaskStatus.CLOSED) {
            notificationService.notifyReviewer(newTaskById);
        }
    }
}
