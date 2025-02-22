package ru.yandex.practicum.configuration;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import org.slf4j.LoggerFactory;

public class LogbackConfig {

    public static void configureLogging() {
        // Получаем корневой логгер
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

        // Создаем аппендер для консоли
        ConsoleAppender consoleAppender = new ConsoleAppender();

        // Создаем энкодер с форматом
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} - %msg%n"); // Формат вывода
        encoder.setContext(rootLogger.getLoggerContext());
        encoder.start();

        // Привязываем энкодер к аппендеру
        consoleAppender.setEncoder(encoder);
        consoleAppender.setContext(rootLogger.getLoggerContext());
        consoleAppender.start();

        // Добавляем аппендер к логгеру
        rootLogger.addAppender(consoleAppender);

        // Устанавливаем уровень логирования
        rootLogger.setLevel(Level.DEBUG);  // Уровень DEBUG для всех логов
    }
}
