# Используем официальный образ OpenJDK 22 как базовый образ
FROM openjdk:22-jre-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем ваш JAR файл в образ Docker
COPY target/EventStreams-1.0-SNAPSHOT.jar /app/EventStreams-1.0-SNAPSHOT.jar

# Открываем порт 8080
EXPOSE 8080

# Запускаем ваше JAR приложение
CMD ["java", "-jar", "EventStreams-1.0-SNAPSHOT.jar"]