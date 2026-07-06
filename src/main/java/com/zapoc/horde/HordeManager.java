package com.zapoc.horde;

import com.zapoc.config.ZapocConfig;

public class HordeManager {

    private static int currentDay = 1;

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
        HordeWaveSpawner.start();

        System.out.println("===== HORDE STARTED =====");
        System.out.println("Horde #" + hordeNumber);
        System.out.println("Groups created: " + HordeGroupManager.getGroups().size());
    }

    public static void stopHorde() {

        if (!hordeActive)
            return;

        hordeActive = false;

        HordeWaveSpawner.stop();
        HordeGroupManager.clear();

        System.out.println("===== HORDE ENDED =====");
    }

    public static int getDaysUntilNextHorde() {

        int interval = getHordeInterval();
        int days = interval - ((currentDay - 1) % interval);

        if (days <= 0)
            days = interval;

        return days;
    }

    public static int getHordeNumber() {
        return hordeNumber;
    }

    public static int getHordeInterval() {
        return ZapocConfig.HORDE_INTERVAL_DAYS.get();
    }
}
