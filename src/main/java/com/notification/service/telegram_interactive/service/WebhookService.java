package com.notification.service.telegram_interactive.service;

import com.notification.service.dto.TaskDto;
import com.notification.service.entity.Task;
import com.notification.service.entity.User;
import com.notification.service.mapper.UserMapper;
import com.notification.service.service.TaskService;
import com.notification.service.service.UserService;
import com.notification.service.telegram_interactive.model.MergeRequestModel;
import com.notification.service.telegram_interactive.model.ThresholdModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class WebhookService {

    @Value("${api.base-url}")
    private String apiBaseUrl;

    private final UserService userService;
    private final TaskService taskService;
    private final RestTemplate restTemplate;
    private final UserMapper userMapper;

    public TaskDto createMergeRequest(MergeRequestModel model) {
        TaskDto taskDto = buildTaskDto(
                model.getReviewerId(), model.getDeveloperId(),
                model.getTitle(), model.getLinkToMr()
        );

        ResponseEntity<TaskDto> response = restTemplate.postForEntity(
                apiBaseUrl,
                new HttpEntity<>(taskDto),
                TaskDto.class
        );

        return response.getBody();
    }

    public TaskDto createThreshold(ThresholdModel model) {
        Task taskByLink = taskService.getTaskByLink(model.getLinkToMr());
        TaskDto taskDto = buildTaskDto(
                model.getReviewerId(), model.getDeveloperId(),
                model.getMrTitle(), model.getLinkToMr()
        );

        String createThresholdUrl = String.format("%s/%d/threshold", apiBaseUrl, taskByLink.getId());
        ResponseEntity<TaskDto> response = restTemplate.postForEntity(
                createThresholdUrl,
                new HttpEntity<>(taskDto),
                TaskDto.class
        );

        return response.getBody();
    }

    public TaskDto createThresholdChange(ThresholdModel model) {
        Task taskByLink = taskService.getTaskByLink(model.getLinkToMr());
        TaskDto taskDto = buildTaskDto(
                model.getReviewerId(), model.getDeveloperId(),
                model.getMrTitle(), model.getLinkToMr()
        );

        String createThresholdUrl = String.format("%s/%d/fix", apiBaseUrl, taskByLink.getId());
        ResponseEntity<TaskDto> response = restTemplate.postForEntity(
                createThresholdUrl,
                new HttpEntity<>(taskDto),
                TaskDto.class
        );

        return response.getBody();
    }

    public TaskDto createMergeRequestMerging(MergeRequestModel model) {
        Task taskByLink = taskService.getTaskByLink(model.getLinkToMr());
        TaskDto taskDto = buildTaskDto(
                model.getReviewerId(), model.getDeveloperId(),
                model.getTitle(), model.getLinkToMr()
        );

        String createThresholdUrl = String.format("%s/%d/merge", apiBaseUrl, taskByLink.getId());
        ResponseEntity<TaskDto> response = restTemplate.postForEntity(
                createThresholdUrl,
                new HttpEntity<>(taskDto),
                TaskDto.class
        );

        return response.getBody();
    }

    private TaskDto buildTaskDto(Long reviewerId, Long developerId, String title, String link) {
        User reviewer = userService.findById(reviewerId);
        User developer = userService.findById(developerId);

        return TaskDto.builder()
                .title(title)
                .linkToMr(link)
                .reviewer(userMapper.toDto(reviewer))
                .developer(userMapper.toDto(developer))
                .build();
    }
}