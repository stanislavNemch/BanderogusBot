package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class GameData {
    private static final Logger logger = LoggerFactory.getLogger(GameData.class);
    private static final String FILE_PATH = "user_levels.json";
    private static final Gson gson = new Gson();

    /**
     * Завантажує рівні користувачів із файлу.
     * Якщо файл не існує, повертає порожню картку.
     */
    public static Map<Long, Integer> loadUserLevels() {
        // Используем File для проверки существования, чтобы не полагаться только на исключения
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type type = new TypeToken<HashMap<Long, Integer>>(){}.getType();
            Map<Long, Integer> levels = gson.fromJson(reader, type);

            // Перевіряємо, чи не повернув Gson null (це відбувається, якщо файл порожній)
            if (levels == null) {
                logger.warn("File '{}' is empty. Starting with a new set of user levels.", FILE_PATH);
                return new HashMap<>();
            }

            logger.info("Successfully loaded {} user levels from {}", levels.size(), FILE_PATH);
            return levels;
        } catch (IOException e) {
            // Цей блок спрацює, якщо файл не знайдено
            logger.warn("Could not find or read '{}'. Starting with empty user levels.", FILE_PATH);
            return new HashMap<>();
        }
    }

    /**
     * Зберігає рівні користувачів у файлі.
     */
    public static void saveUserLevels(Map<Long, Integer> levels) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(levels, writer);
            logger.info("Successfully saved {} user levels to {}", levels.size(), FILE_PATH);
        } catch (IOException e) {
            logger.error("Failed to save user levels to {}", FILE_PATH, e);
        }
    }
}