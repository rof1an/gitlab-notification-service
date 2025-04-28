# Telegram notification service
Сервис предназначен для упрощения приёма и передачи задач в проекте путем уведомления разработчика/ревьюера 
о проверке/поступлении Merge Reqeust'а в телеграм.

## Цель
Документ описывает процесс приёма и передачи задач в проекте, включая этапы проверки, внесения исправлений и мержинга кода.
Подробнее см. [процесс работы](PROCESS.md).

## Deployment
Перед запуском проекта в docker нужно выполнить следующий команды:
1. Собрать `.jar` файл
```shell
./gradlew clean bootJar
```

2. Запуск композа с автоматической сборкой
```shell
docker-compose -f deployment/docker-compose.yml --env-file deployment/docker.compose.env up -d
```

## Uses
Перед запуском приложения следует сделать копию файла .env.example, переименовать её в .env, и затем заполнить
переменные окружения в файле .env. Файл .env.example всегда остаётся в репозитории как пример.


---
Swagger endpoint
```
http://localhost:9095/swagger-ui/index.html
```

## Stack
- Spring (Web, Jpa)
- Flyway
- Mapstruct
- PostgreSQL
- OpenAPI Swagger
- Lombok