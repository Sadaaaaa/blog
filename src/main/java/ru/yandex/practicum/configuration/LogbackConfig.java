package ru.yandex.practicum.configuration;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import org.slf4j.LoggerFactory;

public class LogbackConfig {

    public static void configureLogging() {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        ConsoleAppender consoleAppender = new ConsoleAppender();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} - %msg%n");
        encoder.setContext(rootLogger.getLoggerContext());
        encoder.start();

        consoleAppender.setEncoder(encoder);
        consoleAppender.setContext(rootLogger.getLoggerContext());
        consoleAppender.start();

        rootLogger.addAppender(consoleAppender);

        rootLogger.setLevel(Level.DEBUG);
    }
}
