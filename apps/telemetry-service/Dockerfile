# Многоэтапная сборка для оптимизации размера образа

# Этап 1: Сборка приложения
FROM maven:3.9.5-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Копируем pom.xml и загружаем зависимости (для кеширования слоев)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходный код и собираем приложение
COPY src ./src
RUN mvn clean package -DskipTests -B

# Этап 2: Финальный образ
FROM eclipse-temurin:17-jre-alpine

# Метаданные
LABEL maintainer="warmhouse@example.com"
LABEL description="Telemetry Service"
LABEL version="1.0.0"

# Устанавливаем рабочую директорию
WORKDIR /app

# Создаем непривилегированного пользователя
RUN addgroup -g 1001 -S spring && \
    adduser -S spring -u 1001 -G spring

# Копируем JAR файл из этапа сборки
COPY --from=build /app/target/telemetry-service-*.jar app.jar

# Устанавливаем владельца
RUN chown spring:spring app.jar

# Переключаемся на непривилегированного пользователя
USER spring:spring

# Открываем порт
EXPOSE 8082

# Проверка здоровья контейнера
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8082/actuator/health || exit 1

# JVM параметры для оптимизации
ENV JAVA_OPTS="-Xms128m -Xmx256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Запускаем приложение
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
