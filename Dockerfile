# Используем официальный образ OpenJDK eclipse-temurin:21 как базовый образ
FROM eclipse-temurin:21

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем ваш JAR файл в образ Docker
COPY target/EventStreams-1.0-SNAPSHOT.jar /app/EventStreams-1.0-SNAPSHOT.jar

# Открываем порт 8080
EXPOSE 8080

# Запускаем ваше JAR приложение
CMD ["java", "-jar", "EventStreams-1.0-SNAPSHOT.jar"]