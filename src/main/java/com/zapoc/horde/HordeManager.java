package com.zapoc.horde;

public class HordeManager {

    // Текущий игровой день
    private static int currentDay = 1;

    // Каждые 7 дней начинается орда
    private static final int HORDE_INTERVAL = 7;

    // Активна ли сейчас орда
    private static boolean hordeActive = false;

    // Номер текущей орды
    private static int hordeNumber = 0;

    public static int getCurrentDay() {
        return currentDay;
    }

    public static void setCurrentDay(int day) {
        currentDay = day;
    }

    public static boolean isHordeActive() {
        return hordeActive;
    }

    public static void startHorde() {

        if (hordeActive)
            return;

        hordeActive = true;
        hordeNumber++;

        System.out.println("===== HORDE STARTED =====");
        System.out.println("Horde #" + hordeNumber);
    }

    public static void stopHorde() {

        if (!hordeActive)
            return;

        hordeActive = false;

        System.out.println("===== HORDE ENDED =====");
    }

    public static int getDaysUntilNextHorde() {

        int days = HORDE_INTERVAL - (currentDay % HORDE_INTERVAL);

        if (days == 0)
            days = HORDE_INTERVAL;

        return days;
    }

    public static int getHordeNumber() {
        return hordeNumber;
    }

}
