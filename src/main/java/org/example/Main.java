package org.example;

// Імпортуємо класи для логування
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.util.*;

import static java.util.Arrays.asList;

public class Main extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    // Константи для callback-даних
    private static final String LEVEL_1_TASK = "level_1_task";
    private static final String LEVEL_2_TASK = "level_2_task";
    private static final String LEVEL_3_TASK = "level_3_task";
    private static final String LEVEL_4_TASK = "level_4_task";

    // Сховище рівнів користувачів завантажується з файлу
    private final Map<Long, Integer> userLevels = GameData.loadUserLevels();

    public Main() {
        super(BotConfig.getBotToken());
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        Main bot = new Main();
        api.registerBot(bot);
        logger.info("Telegram Bot is running!");

        // Реєструємо "пастку вимкнення" для збереження даних
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down. Saving user levels...");
            GameData.saveUserLevels(bot.userLevels);
            logger.info("User levels saved. Goodbye!");
        }));
    }

    @Override
    public String getBotUsername() {
        return BotConfig.getBotUsername();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = getChatId(update);
        if (chatId == null) {
            return;
        }

        try {
            if (update.hasMessage() && "/start".equals(update.getMessage().getText())) {
                handleStartCommand(chatId);
            } else if (update.hasCallbackQuery()) {
                handleCallback(update.getCallbackQuery().getData(), chatId);
            }
        } catch (TelegramApiException e) {
            logger.error("API error processing update for chat {}: {}", chatId, e.getMessage(), e);
        }
    }

    private void handleCallback(String callbackData, Long chatId) throws TelegramApiException {
        int userLevel = getLevel(chatId);

        switch (callbackData) {
            case LEVEL_1_TASK:
                if (userLevel == 1) handleLevelUp(chatId, 2);
                break;
            case LEVEL_2_TASK:
                if (userLevel == 2) handleLevelUp(chatId, 3);
                break;
            case LEVEL_3_TASK:
                if (userLevel == 3) handleLevelUp(chatId, 4);
                break;
            case LEVEL_4_TASK:
                if (userLevel == 4) handleFinalTask(chatId);
                break;
            default:
                logger.warn("Unknown callback received: {}", callbackData);
                break;
        }
    }

    private void handleStartCommand(Long chatId) throws TelegramApiException {
        setLevel(chatId, 1);

        String text = "Ґа-ґа-ґа!\n" +
                "Вітаємо у боті біолабораторії «Батько наш Бандера».\n\n" +
                "Ти отримуєш гусака №71\n\n" +
                "Цей бот ми створили для того, щоб твій гусак прокачався з рівня звичайної свійської худоби до рівня біологічної зброї, здатної нищити ворога. \n\n" +
                "Щоб звичайний гусак перетворився на бандерогусака, тобі необхідно:\n" +
                "✔️виконувати завдання\n" +
                "✔️переходити на наступні рівні\n" +
                "✔️заробити достатню кількість монет, щоб придбати Джавеліну і зробити свєрхтра-та-та.\n\n" +
                "*Гусак звичайний.* Стартовий рівень.\n" +
                "Бонус: 5 монет.\n" +
                "Обери завдання, щоб перейти на наступний рівень";

        List<String> buttonTexts = getRandomThree(asList(
                "Сплести маскувальну сітку (+15 монет)",
                "Зібрати кошти патріотичними піснями (+15 монет)",
                "Вступити в Міністерство Мемів України (+15 монет)",
                "Запустити волонтерську акцію (+15 монет)",
                "Вступити до лав тероборони (+15 монет)"
        ));

        Map<String, String> buttons = Map.of(
                buttonTexts.get(0), LEVEL_1_TASK,
                buttonTexts.get(1), LEVEL_1_TASK,
                buttonTexts.get(2), LEVEL_1_TASK
        );

        // Відправляємо анімацію, текст та кнопки одним запитом
        sendAnimation("level-1", chatId, text, createMarkup(buttons));
    }

    private void handleLevelUp(Long chatId, int newLevel) throws TelegramApiException {
        setLevel(chatId, newLevel);

        String text;
        Map<String, String> buttons;

        switch (newLevel) {
            case 2:
                text = "*Вітаємо на другому рівні! Твій гусак - біогусак.*\nБаланс: 20 монет. \nОбери завдання, щоб перейти на наступний рівень";
                buttons = createLevelButtons(asList("Зібрати комарів для нової біологічної зброї (+15 монет)", "Пройти курс молодого бійця (+15 монет)", "Задонатити на ЗСУ (+15 монет)", "Збити дрона банкою огірків (+15 монет)", "Зробити запаси коктейлів Молотова (+15 монет)"), LEVEL_2_TASK);
                break;
            case 3:
                text = "*Вітаємо на третьому рівні! Твій гусак - бандеростажер.*\nБаланс: 35 монет. \nОбери завдання, щоб перейти на наступний рівень";
                buttons = createLevelButtons(asList("Злітати на тестовий рейд по чотирьох позиціях (+15 монет)", "Відвезти гуманітарку на передок (+15 монет)", "Знайти зрадника та здати в СБУ (+15 монет)", "Навести арту на орків (+15 монет)", "Притягнути танк трактором (+15 монет)"), LEVEL_3_TASK);
                break;
            case 4:
                text = "*Вітаємо на останньому рівні! Твій гусак - готова біологічна зброя - бандерогусак.*\nБаланс: 50 монет. \nТепер ти можеш придбати Джавелін і глушити чмонь";
                buttons = Map.of("Купити Джавелін (50 монет)", LEVEL_4_TASK);
                break;
            default:
                logger.warn("Attempted to level up to an unknown level: {}", newLevel);
                return;
        }

        sendAnimation("level-" + newLevel, chatId, text, createMarkup(buttons));
    }

    private void handleFinalTask(Long chatId) throws TelegramApiException {
        setLevel(chatId, 5);
        String text = "*Джавелін твій. Повний вперед!*";
        // Надсилаємо фінальну анімацію з підписом, без кнопок
        sendAnimation("final", chatId, text, null);
    }

    /**
     * Створює Map для кнопок рівня, вибираючи 3 випадкові завдання.
     */
    private Map<String, String> createLevelButtons(List<String> tasks, String callbackData) {
        List<String> randomTasks = getRandomThree(tasks);
        return Map.of(
                randomTasks.get(0), callbackData,
                randomTasks.get(1), callbackData,
                randomTasks.get(2), callbackData
        );
    }

    /**
     * Відправляє анімацію з можливістю додати підпис та кнопки.
     * @param name Ім'я файлу анімації (без .gif)
     * @param chatId ID чату
     * @param caption Текст підпису під анімацією (підтримує Markdown)
     * @param markup Клавіатура з кнопками
     */
    public void sendAnimation(String name, Long chatId, String caption, InlineKeyboardMarkup markup) throws TelegramApiException {
        SendAnimation animation = new SendAnimation();
        animation.setChatId(chatId);
        animation.setAnimation(new InputFile(new File("images/" + name + ".gif")));

        if (caption != null) {
            animation.setCaption(caption);
            animation.setParseMode("markdown");
        }
        if (markup != null) {
            animation.setReplyMarkup(markup);
        }

        execute(animation);
    }

    public Long getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        }
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        }
        return null;
    }

    /**
     * Створює об'єкт клавіатури (розмітки) із картки кнопок.
     */
    private InlineKeyboardMarkup createMarkup(Map<String, String> buttons) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (String buttonName : buttons.keySet()) {
            String buttonValue = buttons.get(buttonName);

            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(buttonName);
            button.setCallbackData(buttonValue);

            keyboard.add(List.of(button));
        }
        markup.setKeyboard(keyboard);
        return markup;
    }

    public int getLevel(Long chatId) {
        return userLevels.getOrDefault(chatId, 0);
    }

    public void setLevel(Long chatId, int level) {
        userLevels.put(chatId, level);
    }

    public List<String> getRandomThree(List<String> variants) {
        ArrayList<String> copy = new ArrayList<>(variants);
        Collections.shuffle(copy);
        return copy.subList(0, 3);
    }
}