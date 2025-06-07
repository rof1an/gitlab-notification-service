package com.notification.service.telegram_interactive;

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

    public TaskDto createMergeRequest(MergeRequestModel data) {
        User reviewer = userService.findById(data.getReviewerId());
        User developer = userService.findById(data.getDeveloperId());

        TaskDto taskDto = TaskDto.builder()
                .title(data.getTitle())
                .linkToMr(data.getLinkToMr())
                .reviewer(userMapper.toDto(reviewer))
                .developer(userMapper.toDto(developer))
                .build();

        ResponseEntity<TaskDto> response = restTemplate.postForEntity(
                apiBaseUrl,
                new HttpEntity<>(taskDto),
                TaskDto.class
        );

        return response.getBody();
    }

    public TaskDto createThreshold(ThresholdModel thresholdModel) {
        Task taskByLink = taskService.getTaskByLink(thresholdModel.getLinkToMr());
        User reviewer = userService.findById(thresholdModel.getReviewerId());
        User developer = userService.findById(thresholdModel.getDeveloperId());

        TaskDto taskDto = TaskDto.builder()
                .title(thresholdModel.getMrTitle())
                .linkToMr(thresholdModel.getLinkToMr())
                .reviewer(userMapper.toDto(reviewer))
                .developer(userMapper.toDto(developer))
                .build();

        String createThresholdUrl = String.format("%s/%d/threshold", apiBaseUrl, taskByLink.getId());

        ResponseEntity<TaskDto> response = restTemplate.postForEntity(
                createThresholdUrl,
                new HttpEntity<>(taskDto),
                TaskDto.class
        );

        return response.getBody();
    }
}