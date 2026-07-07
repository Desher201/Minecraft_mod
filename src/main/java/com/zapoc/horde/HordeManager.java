package com.zapoc.horde;

import com.zapoc.config.ZapocConfig;
import com.zapoc.message.ApocalypseMessageManager;
import net.minecraftforge.server.ServerLifecycleHooks;

public class HordeManager {

    private static int currentDay = 1;

    private static boolean hordeActive = false;
    private static boolean forcedHorde = false;
    private static int hordeNumber = 0;
    private static int suppressedScheduledHordeDay = -1;

    public static int getCurrentDay() {
        return currentDay;
    }

    public static void setCurrentDay(int day) {
        currentDay = Math.max(day, 1);
    }

    public static int calculateDay(long dayTime) {
        int day = (int) (dayTime / 24000L) + 1;

        if (day < 1)
            day = 1;

        return day;
    }

    public static boolean isHordeActive() {
        return hordeActive;
    }

    public static boolean isForcedHorde() {
        return forcedHorde;
    }

    public static void suppressScheduledHordeForDay(int day) {
        suppressedScheduledHordeDay = Math.max(day, 1);
    }

    public static boolean isScheduledHordeSuppressedForDay(int day) {
        return suppressedScheduledHordeDay == day;
    }

    public static void clearScheduledHordeSuppression() {
        suppressedScheduledHordeDay = -1;
    }

    public static int getSuppressedScheduledHordeDay() {
        return suppressedScheduledHordeDay;
    }

    public static void forceStartHorde() {
        clearScheduledHordeSuppression();
        forcedHorde = true;
        startHorde();
    }

    public static void forceStopHorde() {
        forcedHorde = false;
        stopHorde();
    }

    public static void startHorde() {

        if (hordeActive)
            return;

        hordeActive = true;
        hordeNumber++;

        HordeNightEventManager.startForDay(currentDay);
        HordeGroupManager.createGroups();
        HordeWaveSpawner.start();
        ApocalypseMessageManager.sendHordeStartMessages(ServerLifecycleHooks.getCurrentServer());

        System.out.println("===== HORDE STARTED =====");
        System.out.println("Horde #" + hordeNumber);
        System.out.println("Night type: " + HordeNightEventManager.getDisplayName());
        System.out.println("Groups created: " + HordeGroupManager.getGroups().size());
    }

    public static void stopHorde() {

        if (!hordeActive)
            return;

        hordeActive = false;

        HordeWaveSpawner.stop();
        HordeGroupManager.clear();
        HordeNightEventManager.stop();

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
