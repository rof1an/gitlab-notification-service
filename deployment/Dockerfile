FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/hive-notification.jar /app/hive-notification.jar
ENTRYPOINT ["java", "-jar", "hive-notification.jar"]