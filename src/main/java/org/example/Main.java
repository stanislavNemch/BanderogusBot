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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main extends TelegramLongPollingBot {

    //recipe_by_bcstas_bot
    //6173954074:AAFWFuHPHEd38d1OQApuAmSMFOu68SgPvDc
    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(new Main());

        System.out.println("Hello world!");
    }

    @Override
    public String getBotUsername() {
        return "recipe_by_bcstas_bot";
    }

    @Override
    public String getBotToken() {
        return "6173954074:AAFWFuHPHEd38d1OQApuAmSMFOu68SgPvDc";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = getChatId(update);

        if (update.hasMessage() && update.getMessage().getText().equals("/start")){
            SendMessage message = createMessage("Привіт!");
            message.setChatId(chatId);
            attachButtons(message, Map.of(
                    "Слава Україні!!", "glory_for_ukraine"
            ));
            sendApiMethodAsync(message);
        }

        if (update.hasCallbackQuery()){
            if (update.getCallbackQuery().getData().equals("glory_for_ukraine")){
                SendMessage message = createMessage("Героям Слава!");
                message.setChatId(chatId);
                attachButtons(message, Map.of(
                        "Слава Нації", "glory_to_the_nation"
                ));
                sendApiMethodAsync(message);
            }
        }

        if (update.hasCallbackQuery()){
            if (update.getCallbackQuery().getData().equals("glory_to_the_nation")) {
                SendMessage message = createMessage("Смерть ворогам!");
                message.setChatId(chatId);
                sendApiMethodAsync(message);
            }
        }

//        SendMessage msg = createMessage("*Hello* Stanislav!!");
//        attachButtons(msg, Map.of(
//                "BTN_1","hello_btn_1",
//                "BTN_2","hello_btn_2"
//        ));
//        msg.setChatId(chatId);
//        sendApiMethodAsync(msg);
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
        message.setText(new String(text.getBytes(), StandardCharsets.UTF_8));
        message.setParseMode("markdown");
        return message;
    }

    public void attachButtons(SendMessage message, Map<String, String> buttons) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (String buttonName : buttons.keySet()) {
            String buttonValue = buttons.get(buttonName);

            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(new String(buttonName.getBytes(), StandardCharsets.UTF_8));
            button.setCallbackData(buttonValue);

            keyboard.add(List.of(button));
        }

        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);
    }

    public void sendImage(String name, Long chatId){
        SendAnimation animation = new SendAnimation();

        InputFile inputFile = new InputFile();
        inputFile.setMedia(new File("images/" + name + ".gif"));
        executeAsync(animation);
    }
}