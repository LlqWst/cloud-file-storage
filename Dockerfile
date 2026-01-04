# Этап 1: Сборка проекта
FROM eclipse-temurin:23-jdk AS builder
WORKDIR /build

# Копируем исходный код
COPY . .

# Собираем проект без тестов
RUN ./gradlew build -x test --no-daemon

# Этап 2: Запуск приложения
FROM eclipse-temurin:23-jre
WORKDIR /app

# Копируем собранный JAR из этапа сборки
COPY --from=builder /build/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
