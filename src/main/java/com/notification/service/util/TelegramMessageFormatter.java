package com.notification.service.util;


import com.notification.service.entity.Task;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramMessageFormatter {

    public String formatDeveloperMrReviewRequestMessage(Task task) {
        return String.format("""
                        Новый МР: %s
                        Ссылка: %s
                        Подтвердите отправку ревьюеру""",
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

    public String reviewerThresholdOnMrRequestMessage(Task task) {
        return String.format("""
                        Отслежен новый threshold. 
                        МР: %s
                        Developer: %s
                        Ссылка: %s
                        Подтвердите отправку девелоперу""",
                task.getTitle(), task.getDeveloper().getUsername(), task.getLinkToMr());
    }

    public String developerNewFixOnThresholdRequestMessage(Task task) {
        return String.format("""
                        Отслежено новое изменение на threshold
                        Название МР: %s
                        Ссылка: %s
                        Reviewer: %s
                        Подтвердите отправку ревьюеру""",
                task.getTitle(), task.getLinkToMr(), task.getReviewer().getUsername()
        );
    }

    public String formatReviewerNotificationMessage(Task task) {
        return String.format("""
                        Новый MR на проверку: %s
                        Ссылка на Merge Request: %s
                        Developer: %s
                        Reviewer: %s
                        """,
                task.getTitle(),
                task.getLinkToMr(),
                task.getDeveloper().getUsername(),
                task.getReviewer().getUsername()
        );
    }

    public String formatDeveloperNewThresholdMessage(Task task) {
        return String.format("""
                        Новый threshold в МР: %s
                        Нужны исправления.
                        Ссылка на Merge Request: %s
                        Developer: %s
                        Reviewer: %s
                        """,
                task.getTitle(),
                task.getLinkToMr(),
                task.getDeveloper().getUsername(),
                task.getReviewer().getUsername()
        );
    }

    public String formatReviewerNewFixOnThreshold(Task task) {
        return String.format("""
                        Новое изменение по threshold в МР: %s
                        Ссылка на Merge Request: %s
                        Developer: %s
                        Reviewer: %s
                        """,
                task.getTitle(),
                task.getLinkToMr(),
                task.getDeveloper().getUsername(),
                task.getReviewer().getUsername()
        );
    }

    public String startCommand(Update update) {
        return String.format("""
                        Добро пожаловать в бот, %s.
                        Здесь можно увидеть список МР.
                                       
                        Команды для использования:
                        /start - запуск бота
                        """,
                update.getMessage().getChat().getFirstName()
        );
    }

    public String defaultCommand() {
        return """
                Команды для использования:
                /start - запуск бота
                """;
    }
}
