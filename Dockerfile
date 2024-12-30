# Используем официальный образ JDK
FROM openjdk:17-jdk-slim

# Указываем рабочую директорию в контейнере
WORKDIR /app

# Копируем jar файл в контейнер
COPY build/libs/Restaurant_Service-0.0.1-SNAPSHOT.jar app.jar

# Указываем команду запуска
ENTRYPOINT ["java", "-jar", "app.jar"]
