package com.notification.service.telegram;

import com.notification.service.entity.Task;
import com.notification.service.util.MergeRequestDataPrints;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramStub {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String SYSTEM_URL = "http://localhost:8085/api/tasks";

    private List<Task> tasks = new ArrayList<>();

    public void getDeveloperMessage(List<Task> newTasks) {
        MergeRequestDataPrints.notifyToDeveloperTelegram(newTasks);
        tasks.addAll(newTasks);
    }

    @Transactional
    public void notifySystem() {
        List<Long> reviewersId = tasks.stream()
                .map(task -> task.getReviewer().getId())
                .toList();

        reviewersId.forEach(id -> {
            List<Task> preparedTasks = tasks.stream()
                    .peek(task -> task.getReviewer().getId())
                    .toList();

            String url = String.format("%s/%d/notify-reviewer", SYSTEM_URL, id);
            restTemplate.postForEntity(url, preparedTasks, Void.class);
            System.out.println("Отправка уведомления системе: " + url);
        });
    }

    public void getReviewerMessage(List<Task> tasks) {
        MergeRequestDataPrints.notifyToReviewerTelegram(tasks);
    }
}
