package com.notification.service.controller;

import com.notification.service.dto.CreateTaskDto;
import com.notification.service.dto.TaskDto;
import com.notification.service.entity.Task;
import com.notification.service.mapper.TaskMapper;
import com.notification.service.model.TaskStatus;
import com.notification.service.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskMapper mapper;
    private final TaskService taskService;

    @PostMapping
    public TaskDto createTask(@RequestBody CreateTaskDto taskDto) {
        Task savedTask = taskService.createFakeTask(mapper.toModel(taskDto));
        return mapper.toDto(savedTask);
    }

    @GetMapping
    public List<TaskDto> getTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return mapper.toDtoList(tasks);
    }

    @GetMapping("/{id}/develop-tasks")
    public List<TaskDto> getMyTasksOnDevelop(@PathVariable("id") Long id) {
        List<Task> tasks = taskService.getMyDevelopTasks(id);
        return mapper.toDtoList(tasks);
    }

    @GetMapping("/{id}/review-tasks")
    public List<TaskDto> getMyTasksOnReview(@PathVariable("id") Long id) {
        List<Task> tasks = taskService.getMyReviewTasks(id);
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
