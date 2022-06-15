package GameBot;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Reminder {
    private static int totalIds;

    private int id;
    private long chatId;
    private String description;
    private LocalDateTime time = LocalDateTime.now();
    private TimeMode mode = TimeMode.DAY;

    public void setMode(TimeMode mode) {
        this.mode = mode;
    }

    private List<Reminder> reminderList = new ArrayList<>();

    public LocalDateTime getRemindDateTime() {
        switch (mode) {
            case HOUR -> {
                return time.plusHours(1);
            }
            case DAY -> {
                return time.plusHours(20);
            }
            case WEEK -> {
                return time;
            }
            case NEVER -> {
                return time.plusYears(9999);
            }
        }
        return time;
    }

    public String getDescription() {
        return description;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Reminder(String description) {
        this.description = description;
        this.time = LocalDateTime.now();
        this.id = getTotalIds();

        reminderList.add(this);
    }

    public static int getTotalIds() {
        Reminder.totalIds++;
        return totalIds;
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", description='" + description + '\'' +
                ", time=" + time +
                ", mode=" + mode +
                '}';
    }
}
