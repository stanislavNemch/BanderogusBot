package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class BotConfig {
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE_PATH = "config.properties";

    static {
        // Используем try-with-resources для автоматического закрытия потоков
        try (InputStream input = new FileInputStream(CONFIG_FILE_PATH);
             // Используем InputStreamReader, чтобы явно указать кодировку UTF-8
             InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {

            properties.load(reader);

        } catch (IOException ex) {
            System.err.println("Ошибка: Не удалось загрузить файл конфигурации '" + CONFIG_FILE_PATH + "'");
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public static String getBotUsername() {
        // trim() удалит случайные пробелы, если они есть в файле
        return properties.getProperty("bot.username").trim();
    }

    public static String getBotToken() {
        return properties.getProperty("bot.token").trim();
    }
}