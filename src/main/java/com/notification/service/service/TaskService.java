package com.notification.service.service;

import com.notification.service.entity.Task;
import com.notification.service.model.TaskStatus;
import com.notification.service.repository.TaskRepository;
import com.notification.service.util.TaskLogger;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final NotificationService notificationService;

    private final TaskLogger taskLogger;

    public Task createTask(Task task) {
        task.setStatus(TaskStatus.OPEN);
        notificationService.notifyDeveloper(task);
        return taskRepository.save(task);
    }

    public void notifyThresholdTask(Long taskId) {
        Task task = getTaskById(taskId);

        if (task.getStatus() != TaskStatus.CLOSED) {
            notificationService.notifyThresholdReviewer(task);
        }
    }

    public void acceptThresholdTaskNotify(Long taskId){
        Task task = getTaskById(taskId);

        if (task.getStatus() != TaskStatus.CLOSED) {
            task.setStatus(TaskStatus.NEED_FIXES);
            notificationService.acceptThresholdTaskNotify(task);
        }
    }

    public Task mergedTask(Long id) {
        Task taskById = getTaskById(id);
        taskById.setStatus(TaskStatus.CLOSED);
        return taskRepository.save(taskById);
    }

//    public Task getTaskByUser(Long userId, UserRole role) {
//        if (Optional.ofNullable(role).isPresent()) {
//            if (role.equals(UserRole.DEVELOPER)) {
//                Task task = taskRepository.findByDeveloperId(userId);
//                taskLogger.logTask(task, UserRole.DEVELOPER);
//
//                return task;
//            }
//
//            if (role.equals(UserRole.REVIEWER)) {
//                Task task = taskRepository.findByReviewerId(userId);
//                taskLogger.logTask(task, UserRole.REVIEWER);
//
//                return task;
//            }
//        }
//
//        throw new IllegalArgumentException("User role cannot be null");
//    }

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

    public Task notifyReviewerTask(Long taskId) {
        Task task = getTaskById(taskId);

        if (task.getStatus() == TaskStatus.REVIEW) {
            notificationService.notifyReviewer(task);
        }

        return task;
    }
}