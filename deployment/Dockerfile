FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/telegram-notification-bot.jar /app/telegram-notification-bot.jar
ENTRYPOINT ["java", "-jar", "telegram-notification-bot.jar"]