package com.notification.service.controller;

import com.notification.service.dto.TaskDto;
import com.notification.service.entity.Task;
import com.notification.service.mapper.TaskMapper;
import com.notification.service.model.TaskStatus;
import com.notification.service.model.UserRole;
import com.notification.service.service.TaskService;
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
    public TaskDto createTask(@RequestBody TaskDto taskDto) {
        Task savedTask = taskService.createTask(mapper.toModel(taskDto));
        return mapper.toDto(savedTask);
    }

    @PostMapping("/{taskId}/notify-task/developer")
    public void notifyDeveloper(@PathVariable("taskId") Long taskId) {
        taskService.notifyDeveloperTask(taskId);
    }

    @PostMapping("/{taskId}/notify-task/reviewer")
    public void notifyReviewer(@PathVariable("taskId") Long taskId) {
        taskService.notifyReviewerTask(taskId);
    }

    @PostMapping("/{taskId}/merge")
    public TaskDto mergedTask(@PathVariable("taskId") Long id) {
        Task mergedTask = taskService.mergedTask(id);
        return mapper.toDto(mergedTask);
    }

    @PostMapping("/{taskId}/threshold")
    public void thresholdTask(@PathVariable("taskId") Long taskId) {
        taskService.notifyThresholdTask(taskId);
    }

    @GetMapping
    public List<TaskDto> getTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return mapper.toDtoList(tasks);
    }

    @GetMapping("{id}/user")
    public List<TaskDto> getTasksByUser(@PathVariable("id") Long id,
                                        @RequestParam(value = "status", required = false) UserRole role) {
        List<Task> tasks = taskService.getTasksByUser(id, role);
        return mapper.toDtoList(tasks);
    }

    @GetMapping("/{id}")
    public TaskDto getTaskById(@PathVariable("id") Long id) {
        Task taskById = taskService.getTaskById(id);
        return mapper.toDto(taskById);
    }

    @PutMapping("/{id}")
    public TaskDto updateTask(@PathVariable("id") Long id, @RequestParam TaskStatus status) {
        Task updatedTask = taskService.updateTaskStatus(id, status);
        return mapper.toDto(updatedTask);
    }
}
