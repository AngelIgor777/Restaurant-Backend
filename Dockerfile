FROM openjdk:17-jdk-slim AS build

WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle

COPY build.gradle settings.gradle ./
COPY src src

RUN chmod +x gradlew

RUN ./gradlew build -x test

RUN apt-get update && apt-get install -y \
    libfreetype6 \
    fontconfig \
    libx11-6 \
    && rm -rf /var/lib/apt/lists/* \

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 9091

ENTRYPOINT ["java", "-jar", "app.jar"]
