package com.notification.service.service;

import com.notification.service.entity.Task;
import com.notification.service.model.TaskStatus;
import com.notification.service.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public Task createFakeTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> getMyDevelopTasks(long developerId) {
        List<Task> tasks = taskRepository.findAllByDeveloperId(developerId);

        tasks.forEach(task -> {
            String logMessage = String.format("""
                             Новая задача на проверку!
                             Название: %s
                             Разработчик: %s
                             Проверяющий: %s
                             Ссылка на MR: %s
                             Статус: %s
                            """,
                    task.getTitle(),
                    task.getDeveloper().getUsername(),
                    task.getReviewer().getUsername(),
                    task.getLinkToMr(),
                    task.getStatus()
            );

            System.out.println(logMessage);
        });

        return tasks;
    }

    public List<Task> getMyReviewTasks(long developerId) {
        List<Task> tasks = taskRepository.findAllByReviewerId(developerId);

        tasks.forEach(task -> {
            String logMessage = String.format("""
                             Получен новый MR!
                             Название: %s
                             Разработчик: %s
                             Проверяющий: %s
                             Ссылка на MR: %s
                             Статус: %s
                            """,
                    task.getTitle(),
                    task.getDeveloper().getUsername(),
                    task.getReviewer().getUsername(),
                    task.getLinkToMr(),
                    task.getStatus()
            );

            System.out.println(logMessage);
        });

        return tasks;
    }

    public Task getTaskById(long id) {
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
}
