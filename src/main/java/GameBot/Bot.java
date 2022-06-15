package GameBot;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.time.LocalDateTime;
import java.util.*;


public class Bot extends TelegramLongPollingBot {

    List<Reminder> users = new ArrayList<>();

    public Bot(DefaultBotOptions defaultBotOptions) {
        super(defaultBotOptions);
    }// хз зачем эта штука


    public static void main(String[] args) throws TelegramApiException {
        Bot bot = new Bot(new DefaultBotOptions());
//        bot.execute(SendMessage.builder().chatId("445978060").text("Hello from java Petr").build());
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class); // чтоб зарегать бота
        telegramBotsApi.registerBot(bot); // чтоб зарегать бота

        bot.remindCheсker();
        bot.ping();
//        bot.mySendMessage();
    }

    private void ping() {
            while (true) {
                try {
                    Thread.sleep(100000);
                } catch (InterruptedException ignored) {}
                new PingTask().pingMe();
        }
    }

    @Override
    public String getBotUsername() {
        return "@Reminder";
    }

    @Override
    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            handleMessage(update.getMessage());
        }

//        // нажатие кнопки
//        if (update.hasCallbackQuery()) {
//            try {
//                handleCallback(update.getCallbackQuery());
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
    }

    private void remindCheсker() {
        while (true) {
            try {
                System.out.println("check");
                Thread.sleep(10000);
                for (Reminder reminder : users) {
                    if (LocalDateTime.now().isAfter(reminder.getRemindDateTime())) {
                        execute(SendMessage.builder()
                                .text("Привет! Ты просил напомнить тебе " + reminder.getDescription().toLowerCase() + " :)")
                                .chatId(String.valueOf(reminder.getChatId()))
                                .build());
                        reminder.setMode(TimeMode.NEVER);
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void handleMessage(Message message) {
        if (Process.isReminderCreatingStep1()) {
            Reminder reminder = new Reminder(message.getText());
            reminder.setChatId(message.getChatId());
            users.add(reminder);

            Process.setReminderCreatingStep1(false);
            Process.setReminderCreatingStep2(true);
        }

        if (Process.isReminderCreatingStep2()) {
            sendTimeModeButtons(message);

            Process.setReminderCreatingStep2(false);
            Process.setReminderCreatingStep3(true);
            return;
        }

        if (Process.isReminderCreatingStep3()) {
            Process.setReminderCreatingStep3(false);
            switch (message.getText()) {
                case ("Через час"):
                    for (Reminder reminder : users) {
                        if (reminder.getChatId() == message.getChatId()) {
                            reminder.setMode(TimeMode.HOUR);
                        }
                    }
                    break;
                case ("Завтра"):
                    for (Reminder reminder : users) {
                        if (reminder.getChatId() == message.getChatId()) {
                            reminder.setMode(TimeMode.DAY);
                        }
                    }
                    break;
                case ("Через неделю"):
                    for (Reminder reminder : users) {
                        if (reminder.getChatId() == message.getChatId()) {
                            reminder.setMode(TimeMode.WEEK);
                        }
                    }
                    break;
            }
            System.out.println(users.get(0).toString());
            mySendMessage(message, "Отлично! \nЯ точно не забуду :)\nНапомню " + message.getText().toLowerCase());
            sendNewRemindJoiner(message);
            sendButtonsRemind(message);
            return;
        }

        if (message.getText().contains("/start")) {
            startingMessage(message);
            sendButtonsRemind(message);
            return;
        }

        if (message.getText().contains("напоминание")) {
            mySendMessage(message, "Что нужно напомнить?\nнапример - выгулять собаку");
            Process.setReminderCreatingStep1(true);
            return;
        }

        sendRules(message);
//        if (message.hasText()) {
//            mySendMessage(message, "Ваш текст: " + message.getText());
//        }
    }

    private void sendRules(Message message) {
        mySendMessage(message, "К сожалению, бот пока не умеет разговаривать... \nЗато он умеет напоминать!");
        sendButtonsRemind(message);
    }

    private void sendNewRemindJoiner(Message message) {
        mySendMessage(message, "Напомнить что-нибудь еще?");
    }

    private void handleCallback(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        String[] data = callbackQuery.getData().split(":"); // в дате зашито action : parameter
        String action = data[0];
        int parameter = Integer.valueOf(data[1]);
        System.out.println("sdffsf");

        if (action.equals("BUTTONS_1")) {
            switch (parameter) {
                case (1) -> mySendMessage(message, "Вы нажали кнопку 1");
                case (2) -> mySendMessage(message, "Вы нажали кнопку 2");
                case (3) -> mySendMessage(message, "Вы нажали кнопку 3");
                case (4) -> mySendMessage(message, "Вы нажали кнопку 4");
            }
        }
    }

    private void startingMessage(Message message) {
        mySendMessage(message, "Привет! Этот бот может напомнить тебе о чём-то важном.");
    }

    private void mySendMessage(Message message, String text) {
        try {
            execute(SendMessage.builder()
                    .text(text)
                    .chatId(message.getChatId().toString())
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendButtonsRemind(Message message) {
//        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
//
//        List<InlineKeyboardButton> row1 = new ArrayList<>();
//        row1.add(InlineKeyboardButton.builder().text("Кнопка 1").callbackData("BUTTONS_1:1").build());
//        row1.add(InlineKeyboardButton.builder().text("Кнопка 2").callbackData("BUTTONS_1:2").build());
//
//        List<InlineKeyboardButton> row2 = new ArrayList<>();
//        row2.add(InlineKeyboardButton.builder().text("Кнопка 3").callbackData("BUTTONS_1:3").build());
//        row2.add(InlineKeyboardButton.builder().text("Кнопка 4").callbackData("BUTTONS_1:4").build());
//
//        buttons.add(row1);
//        buttons.add(row2);

        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(KeyboardButton.builder().text("Создать напоминание").build());
//        firstRow.add(KeyboardButton.builder().text("Кнопка 2").build());

//        KeyboardRow secondRow = new KeyboardRow();
//        secondRow.add(KeyboardButton.builder().text("Назад").build());
//        secondRow.add(KeyboardButton.builder().text("Кнопка 4").build());

        List<KeyboardRow> buttons1 = new ArrayList<>();
        buttons1.add(firstRow);
//        buttons1.add(secondRow);


        try {
            execute(SendMessage.builder()
                    .text("Чтобы создать напоминание, нажми на кнопку ниже: ")
                    .chatId(message.getChatId().toString())
                    .replyMarkup(ReplyKeyboardMarkup.builder()
                            .keyboard(buttons1)
                            .resizeKeyboard(true)
                            .oneTimeKeyboard(true)
                            .build())
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendTimeModeButtons(Message message) {
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(KeyboardButton.builder().text("Через час").build());

        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add(KeyboardButton.builder().text("Завтра").build());

        KeyboardRow thirdRow = new KeyboardRow();
        thirdRow.add(KeyboardButton.builder().text("Через неделю").build());

        List<KeyboardRow> buttons2 = new ArrayList<>();
        buttons2.add(firstRow);
        buttons2.add(secondRow);
        buttons2.add(thirdRow);


        try {
            execute(SendMessage.builder()
                    .text("Хорошо, когда напомнить?")
                    .chatId(message.getChatId().toString())
                    .replyMarkup(ReplyKeyboardMarkup.builder()
                            .keyboard(buttons2)
                            .resizeKeyboard(true)
                            .oneTimeKeyboard(true)
                            .build())
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}

