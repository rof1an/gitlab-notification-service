package com.notification.service.util;


import com.notification.service.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TelegramMessageFormatter {

    public String formatDeveloperMrReviewRequestMessage(Task task) {
        return String.format("""
                        Новый МР/Новые изменения в МР: %s
                        Ссылка: %s"
                        "Подтвердите отправку ревьюеру""",
                task.getTitle(), task.getLinkToMr());
    }

    public String formatReviewerNewMrMessage(Task task) {
        return String.format("""
                        Новая задача на ревью: %s
                        Ссылка: %s""",
                task.getTitle(), task.getLinkToMr());
    }

    public String formatDeveloperMergedMrMessage(Task task) {
        return String.format("""
                        Ваш MR был успешно смержен!: %s
                        Ссылка: %s
                        Reviewer: %s""",
                task.getTitle(), task.getLinkToMr(), task.getReviewer().getUsername());
    }

    public String reviewerThresholdOnMrRequestMessage() {
        return "Отслежен новый threshold, подтвердите отправку девелоперу";
    }

    public String developerNewFixOnThresholdRequestMessage() {
        return "Отслежено новое изменение на threshold, подтвердите отправку ревьюеру";
    }
}
