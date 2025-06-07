package com.notification.service.controller;

import com.notification.service.dto.TaskDto;
import com.notification.service.entity.Task;
import com.notification.service.mapper.TaskMapper;
import com.notification.service.model.TaskStatus;
import com.notification.service.model.UserRole;
import com.notification.service.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskMapper mapper;

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Создать новый MR")
    public TaskDto createTask(@RequestBody TaskDto taskDto) {
        Task savedTask = taskService.createTask(mapper.toModel(taskDto));
        return mapper.toDto(savedTask);
    }

    @PostMapping("/{taskId}/notify-task/reviewer")
    @Operation(summary = "Уведомить ревьюера о новом MR")
    public void notifyReviewer(@PathVariable("taskId") Long taskId) {
        taskService.notifyReviewerTask(taskId);
    }

    @PostMapping("/{taskId}/merge")
    @Operation(summary = "Смержить МР")
    public TaskDto mergeTask(@PathVariable("taskId") Long id) {
        Task mergedTask = taskService.mergeTask(id);
        return mapper.toDto(mergedTask);
    }

    @PostMapping("/{taskId}/threshold")
    @Operation(summary = "Эмуляция создания threshold в MR")
    public TaskDto thresholdTask(@PathVariable("taskId") Long taskId) {
        Task task = taskService.notifyThresholdTask(taskId);
        return mapper.toDto(task);
    }

    @PostMapping("/{taskId}/accept-threshold")
    @Operation(summary = "Подтвердить отправку threshold для девелопера")
    public void acceptThresholdTaskNotify(@PathVariable("taskId") Long taskId) {
        taskService.confirmThresholdTaskNotify(taskId);
    }

    @PostMapping("/{taskId}/fix")
    @Operation(summary = "Отправка изменения в МР по трешхолду")
    public void sendNewFixOnThreshold(@PathVariable("taskId") Long taskId) {
        taskService.sendNewFixOnThreshold(taskId);
    }

    @GetMapping
    @Operation(summary = "Получить все MR")
    public List<TaskDto> getTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return mapper.toDtoList(tasks);
    }

    @GetMapping("/by-link")
    @Operation(summary = "Получить МР по его ссылке")
    public TaskDto getTaskByLink(@RequestBody String link) {
        Task taskByLink = taskService.getTaskByLink(link);
        return mapper.toDto(taskByLink);
    }

    @GetMapping("{id}/user")
    @Operation(summary = "Получить MR для пользователя")
    public TaskDto getTaskByUser(@PathVariable("id") Long userId,
                                 @RequestParam(value = "status", required = false) UserRole role) {
        Task task = taskService.getTaskByUser(userId, role);
        return mapper.toDto(task);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить MR по айди")
    public TaskDto getTaskById(@PathVariable("id") Long id) {
        Task taskById = taskService.getTaskById(id);
        return mapper.toDto(taskById);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные о MR")
    public TaskDto updateTask(@PathVariable("id") Long id, @RequestParam TaskStatus status) {
        Task updatedTask = taskService.updateTaskStatus(id, status);
        return mapper.toDto(updatedTask);
    }
}
