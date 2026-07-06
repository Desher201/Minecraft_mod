package com.zapoc.horde;

import java.util.Random;

public class HordeNightEventManager {

    private static final Random RANDOM = new Random();

    private static HordeNightType currentType = HordeNightType.MIXED;

    public static void startForDay(int day) {

        currentType = selectForDay(day);

        System.out.println("[ZApoc] Horde night type: " + getDisplayName());
    }

    public static void stop() {
        currentType = HordeNightType.MIXED;
    }

    public static HordeNightType getCurrentType() {
        return currentType;
    }

    public static String getDisplayName() {
        switch (currentType) {
            case BREAKER_SIEGE:
                return "Breaker Siege";
            case RUNNER_RUSH:
                return "Runner Rush";
            case CRAWLER_SWARM:
                return "Crawler Swarm";
            case TANK_PUSH:
                return "Tank Push";
            case HUNTER_NIGHT:
                return "Hunter Night";
            case FINAL_HORDE:
                return "Final Horde";
            case MIXED:
            default:
                return "Mixed";
        }
    }

    public static boolean isFinalHorde() {
        return currentType == HordeNightType.FINAL_HORDE;
    }

    private static HordeNightType selectForDay(int day) {

        if (day >= 100 && day % 100 == 0)
            return HordeNightType.FINAL_HORDE;

        int roll = RANDOM.nextInt(100);

        if (day >= 60) {
            if (roll < 35)
                return HordeNightType.MIXED;
            if (roll < 50)
                return HordeNightType.RUNNER_RUSH;
            if (roll < 65)
                return HordeNightType.BREAKER_SIEGE;
            if (roll < 80)
                return HordeNightType.CRAWLER_SWARM;
            if (roll < 90)
                return HordeNightType.TANK_PUSH;
            return HordeNightType.HUNTER_NIGHT;
        }

        if (day >= 40) {
            if (roll < 45)
                return HordeNightType.MIXED;
            if (roll < 65)
                return HordeNightType.RUNNER_RUSH;
            if (roll < 85)
                return HordeNightType.BREAKER_SIEGE;
            return HordeNightType.CRAWLER_SWARM;
        }

        if (day >= 20) {
            if (roll < 55)
                return HordeNightType.MIXED;
            if (roll < 80)
                return HordeNightType.RUNNER_RUSH;
            return HordeNightType.BREAKER_SIEGE;
        }

        if (roll < 85)
            return HordeNightType.MIXED;

        return HordeNightType.RUNNER_RUSH;
    }
}
