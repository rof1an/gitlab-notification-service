package com.notification.service.telegram;

import com.notification.service.entity.Task;
import com.notification.service.service.TaskService;
import com.notification.service.util.MergeRequestDataPrints;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class TelegramStub {

    @Lazy
    private final TaskService taskService; // TODO - убрать цикл. зависимость

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String SYSTEM_URL = "http://localhost:8085/api/tasks";

    private Task task = new Task();

    public void getDeveloperMessage(Task newTask) {
        MergeRequestDataPrints.notifyToDeveloperTelegram(newTask);
        this.task = new Task(newTask);
    }

    public void notifySystem() {
        String url = String.format("%s/%d/notify-reviewer", SYSTEM_URL, task.getReviewer().getId());
        restTemplate.postForEntity(url, task.getId(), Void.class);
        System.out.println("Отправка уведомления системе: " + url);
    }

    public void getReviewerMessage(Long taskId) {
        Task taskById = taskService.getTaskById(taskId);
        MergeRequestDataPrints.notifyToReviewerTelegram(taskById);
    }
}
