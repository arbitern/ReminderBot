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
    }// �� ����� ��� �����


    public static void main(String[] args) throws TelegramApiException {
        Bot bot = new Bot(new DefaultBotOptions());
//        bot.execute(SendMessage.builder().chatId("445978060").text("Hello from java Petr").build());
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class); // ���� �������� ����
        telegramBotsApi.registerBot(bot); // ���� �������� ����

        bot.remindChe�ker();
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

//        // ������� ������
//        if (update.hasCallbackQuery()) {
//            try {
//                handleCallback(update.getCallbackQuery());
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
    }

    private void remindChe�ker() {
        while (true) {
            try {
                System.out.println("check");
                Thread.sleep(10000);
                for (Reminder reminder : users) {
                    if (LocalDateTime.now().isAfter(reminder.getRemindDateTime())) {
                        execute(SendMessage.builder()
                                .text("������! �� ������ ��������� ���� " + reminder.getDescription().toLowerCase() + " :)")
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
                case ("����� ���"):
                    for (Reminder reminder : users) {
                        if (reminder.getChatId() == message.getChatId()) {
                            reminder.setMode(TimeMode.HOUR);
                        }
                    }
                    break;
                case ("������"):
                    for (Reminder reminder : users) {
                        if (reminder.getChatId() == message.getChatId()) {
                            reminder.setMode(TimeMode.DAY);
                        }
                    }
                    break;
                case ("����� ������"):
                    for (Reminder reminder : users) {
                        if (reminder.getChatId() == message.getChatId()) {
                            reminder.setMode(TimeMode.WEEK);
                        }
                    }
                    break;
            }
            System.out.println(users.get(0).toString());
            mySendMessage(message, "�������! \n� ����� �� ������ :)\n������� " + message.getText().toLowerCase());
            sendNewRemindJoiner(message);
            sendButtonsRemind(message);
            return;
        }

        if (message.getText().contains("/start")) {
            startingMessage(message);
            sendButtonsRemind(message);
            return;
        }

        if (message.getText().contains("�����������")) {
            mySendMessage(message, "��� ����� ���������?\n�������� - �������� ������");
            Process.setReminderCreatingStep1(true);
            return;
        }

        sendRules(message);
//        if (message.hasText()) {
//            mySendMessage(message, "��� �����: " + message.getText());
//        }
    }

    private void sendRules(Message message) {
        mySendMessage(message, "� ���������, ��� ���� �� ����� �������������... \n���� �� ����� ����������!");
        sendButtonsRemind(message);
    }

    private void sendNewRemindJoiner(Message message) {
        mySendMessage(message, "��������� ���-������ ���?");
    }

    private void handleCallback(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        String[] data = callbackQuery.getData().split(":"); // � ���� ������ action : parameter
        String action = data[0];
        int parameter = Integer.valueOf(data[1]);
        System.out.println("sdffsf");

        if (action.equals("BUTTONS_1")) {
            switch (parameter) {
                case (1) -> mySendMessage(message, "�� ������ ������ 1");
                case (2) -> mySendMessage(message, "�� ������ ������ 2");
                case (3) -> mySendMessage(message, "�� ������ ������ 3");
                case (4) -> mySendMessage(message, "�� ������ ������ 4");
            }
        }
    }

    private void startingMessage(Message message) {
        mySendMessage(message, "������! ���� ��� ����� ��������� ���� � ���-�� ������.");
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
//        row1.add(InlineKeyboardButton.builder().text("������ 1").callbackData("BUTTONS_1:1").build());
//        row1.add(InlineKeyboardButton.builder().text("������ 2").callbackData("BUTTONS_1:2").build());
//
//        List<InlineKeyboardButton> row2 = new ArrayList<>();
//        row2.add(InlineKeyboardButton.builder().text("������ 3").callbackData("BUTTONS_1:3").build());
//        row2.add(InlineKeyboardButton.builder().text("������ 4").callbackData("BUTTONS_1:4").build());
//
//        buttons.add(row1);
//        buttons.add(row2);

        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(KeyboardButton.builder().text("������� �����������").build());
//        firstRow.add(KeyboardButton.builder().text("������ 2").build());

//        KeyboardRow secondRow = new KeyboardRow();
//        secondRow.add(KeyboardButton.builder().text("�����").build());
//        secondRow.add(KeyboardButton.builder().text("������ 4").build());

        List<KeyboardRow> buttons1 = new ArrayList<>();
        buttons1.add(firstRow);
//        buttons1.add(secondRow);


        try {
            execute(SendMessage.builder()
                    .text("����� ������� �����������, ����� �� ������ ����: ")
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
        firstRow.add(KeyboardButton.builder().text("����� ���").build());

        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add(KeyboardButton.builder().text("������").build());

        KeyboardRow thirdRow = new KeyboardRow();
        thirdRow.add(KeyboardButton.builder().text("����� ������").build());

        List<KeyboardRow> buttons2 = new ArrayList<>();
        buttons2.add(firstRow);
        buttons2.add(secondRow);
        buttons2.add(thirdRow);


        try {
            execute(SendMessage.builder()
                    .text("������, ����� ���������?")
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

