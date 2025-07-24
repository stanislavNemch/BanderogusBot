package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class BotConfig {
    // Используем логгер и здесь
    private static final Logger logger = LoggerFactory.getLogger(BotConfig.class);
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE_PATH = "config.properties";

    static {
        try (InputStream input = new FileInputStream(CONFIG_FILE_PATH);
             InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException ex) {
            // Используем логгер вместо System.err
            logger.error("Configuration file '{}' not found or cannot be read.", CONFIG_FILE_PATH, ex);
            // Завершаем программу, так как без конфигурации бот работать не может
            System.exit(1);
        }
    }

    public static String getBotUsername() {
        String username = properties.getProperty("bot.username");
        // Проверяем, что свойство не только существует, но и не пустое
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalStateException("Property 'bot.username' is missing or empty in " + CONFIG_FILE_PATH);
        }
        return username.trim();
    }

    public static String getBotToken() {
        String token = properties.getProperty("bot.token");
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalStateException("Property 'bot.token' is missing or empty in " + CONFIG_FILE_PATH);
        }
        return token.trim();
    }
}