package com.zapoc.horde;

public class HordeManager {

    private static int currentDay = 1;

    private static final int HORDE_INTERVAL = 10;

    private static boolean hordeActive = false;

    private static int hordeNumber = 0;

    public static int getCurrentDay() {
        return currentDay;
    }

    public static void setCurrentDay(int day) {
        currentDay = Math.max(day, 1);
    }

    public static boolean isHordeActive() {
        return hordeActive;
    }

    public static void startHorde() {

        if (hordeActive)
            return;

        hordeActive = true;
        hordeNumber++;

        HordeGroupManager.createGroups();

        System.out.println("===== HORDE STARTED =====");
        System.out.println("Horde #" + hordeNumber);
        System.out.println("Groups created: " + HordeGroupManager.getGroups().size());
    }

    public static void stopHorde() {

        if (!hordeActive)
            return;

        hordeActive = false;

        HordeGroupManager.clear();

        System.out.println("===== HORDE ENDED =====");
    }

    public static int getDaysUntilNextHorde() {

        int days = HORDE_INTERVAL - ((currentDay - 1) % HORDE_INTERVAL);

        if (days <= 0)
            days = HORDE_INTERVAL;

        return days;
    }

    public static int getHordeNumber() {
        return hordeNumber;
    }

    public static int getHordeInterval() {
        return HORDE_INTERVAL;
    }
}
