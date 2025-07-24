package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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

    // Константы для callback-данных
    private static final String LEVEL_1_TASK = "level_1_task";
    private static final String LEVEL_2_TASK = "level_2_task";
    private static final String LEVEL_3_TASK = "level_3_task";
    private static final String LEVEL_4_TASK = "level_4_task";

    // Хранилище уровней пользователей
    private final Map<Long, Integer> userLevels = new HashMap<>();

    // ================== КОНСТРУКТОР ==================
    // Передаем токен в конструктор родительского класса.
    public Main() {
        super(BotConfig.getBotToken());
    }
    // ==============================================================

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(new Main());
        System.out.println("Telegram Bot is running!");
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

        if (update.hasMessage() && "/start".equals(update.getMessage().getText())) {
            try {
                handleStartCommand(chatId);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else if (update.hasCallbackQuery()) {
            try {
                handleCallback(update.getCallbackQuery().getData(), chatId);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
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
                System.out.println("Unknown callback: " + callbackData);
                break;
        }
    }

    private void handleStartCommand(Long chatId) throws TelegramApiException {
        setLevel(chatId, 1);
        sendImage("level-1", chatId);

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
        SendMessage message = createMessage(text);
        message.setChatId(chatId);

        List<String> buttons = getRandomThree(asList(
                "Сплести маскувальну сітку (+15 монет)",
                "Зібрати кошти патріотичними піснями (+15 монет)",
                "Вступити в Міністерство Мемів України (+15 монет)",
                "Запустити волонтерську акцію (+15 монет)",
                "Вступити до лав тероборони (+15 монет)"
        ));

        attachButtons(message, Map.of(
                buttons.get(0), LEVEL_1_TASK,
                buttons.get(1), LEVEL_1_TASK,
                buttons.get(2), LEVEL_1_TASK
        ));
        // ================== ASYNC МЕТОД ==================
        executeAsync(message);
        // =============================================================
    }

    private void handleLevelUp(Long chatId, int newLevel) throws TelegramApiException {
        setLevel(chatId, newLevel);
        sendImage("level-" + newLevel, chatId);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        List<String> buttons;
        String callback;

        switch (newLevel) {
            case 2:
                message.setText("*Вітаємо на другому рівні! Твій гусак - біогусак.*\nБаланс: 20 монет. \nОбери завдання, щоб перейти на наступний рівень");
                buttons = asList("Зібрати комарів для нової біологічної зброї (+15 монет)", "Пройти курс молодого бійця (+15 монет)", "Задонатити на ЗСУ (+15 монет)", "Збити дрона банкою огірків (+15 монет)", "Зробити запаси коктейлів Молотова (+15 монет)");
                callback = LEVEL_2_TASK;
                break;
            case 3:
                message.setText("*Вітаємо на третьому рівні! Твій гусак - бандеростажер.*\nБаланс: 35 монет. \nОбери завдання, щоб перейти на наступний рівень");
                buttons = asList("Злітати на тестовий рейд по чотирьох позиціях (+15 монет)", "Відвезти гуманітарку на передок (+15 монет)", "Знайти зрадника та здати в СБУ (+15 монет)", "Навести арту на орків (+15 монет)", "Притягнути танк трактором (+15 монет)");
                callback = LEVEL_3_TASK;
                break;
            case 4:
                message.setText("*Вітаємо на останньому рівні! Твій гусак - готова біологічна зброя - бандерогусак.*\nБаланс: 50 монет. \nТепер ти можеш придбати Джавелін і глушити чмонь");
                attachButtons(message, Map.of("Купити Джавелін (50 монет)", LEVEL_4_TASK));
                executeAsync(message);
                return;
            default:
                return;
        }

        List<String> randomButtons = getRandomThree(buttons);
        attachButtons(message, Map.of(
                randomButtons.get(0), callback,
                randomButtons.get(1), callback,
                randomButtons.get(2), callback
        ));
        // ================== ASYNC МЕТОД ==================
        executeAsync(message);
        // =============================================================
    }

    private void handleFinalTask(Long chatId) throws TelegramApiException {
        setLevel(chatId, 5);
        sendImage("final", chatId);
        SendMessage message = createMessage("*Джавелін твій. Повний вперед!*");
        message.setChatId(chatId);
        // ================== ASYNC МЕТОД ==================
        executeAsync(message);
        // =============================================================
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

    public SendMessage createMessage(String text) {
        SendMessage message = new SendMessage();
        message.setText(text);
        message.setParseMode("markdown");
        return message;
    }

    public void attachButtons(SendMessage message, Map<String, String> buttons) {
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
        message.setReplyMarkup(markup);
    }

    public void sendImage(String name, Long chatId) {
        try {
            SendAnimation animation = new SendAnimation();
            InputFile inputFile = new InputFile(new File("images/" + name + ".gif"));
            animation.setAnimation(inputFile);
            animation.setChatId(chatId);
            execute(animation);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
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