# Этап сборки
FROM openjdk:17-jdk-slim AS build

# Указываем рабочую директорию для сборки
WORKDIR /app

# Копируем файлы Gradle Wrapper
COPY gradlew gradlew
COPY gradle gradle

# Копируем файлы проекта, кроме тех, которые игнорируются в .dockerignore
COPY build.gradle settings.gradle ./
COPY src src

# Устанавливаем права на выполнение для Gradle Wrapper
RUN chmod +x gradlew

# Выполняем сборку проекта
RUN ./gradlew build --no-daemon

# Этап выполнения
FROM openjdk:17-jdk-slim

# Указываем рабочую директорию для приложения
WORKDIR /app

# Копируем собранный JAR из этапа сборки
COPY --from=build /app/build/libs/*.jar app.jar

# Указываем команду запуска
ENTRYPOINT ["java", "-jar", "app.jar"]
