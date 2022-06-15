package GameBot;

public class Process {

    private static boolean reminderCreatingStep1 = false;
    private static boolean reminderCreatingStep2 = false;

    private static boolean reminderCreatingStep3 = false;

    public static boolean isReminderCreatingStep3() {
        return reminderCreatingStep3;
    }

    public static void setReminderCreatingStep3(boolean reminderCreatingStep3) {
        Process.reminderCreatingStep3 = reminderCreatingStep3;
    }

    public static boolean isReminderCreatingStep2() {
        return reminderCreatingStep2;
    }

    public static void setReminderCreatingStep2(boolean reminderCreatingStep2) {
        Process.reminderCreatingStep2 = reminderCreatingStep2;
    }

    public static boolean isReminderCreatingStep1() {
        return reminderCreatingStep1;
    }

    public static void setReminderCreatingStep1(boolean reminderCreating1) {
        Process.reminderCreatingStep1 = reminderCreating1;
    }
}
