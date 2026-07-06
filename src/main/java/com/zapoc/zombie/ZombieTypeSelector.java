package com.zapoc.zombie;

import java.util.Random;

public class ZombieTypeSelector {

    private static final Random random = new Random();

    public static ZombieType getType(int day) {

        // чем больше день — тем больше шанс сильных зомби
        int roll = random.nextInt(100);

        if (day < 5) {
            return ZombieType.NORMAL;
        }

        if (day >= 20 && roll < 12) return ZombieType.CRAWLER;

        if (roll < 50) return ZombieType.NORMAL;
        if (roll < 70) return ZombieType.RUNNER;
        if (roll < 85) return ZombieType.HUNTER;
        if (roll < 95) return ZombieType.TANK;

        return ZombieType.BREAKER;
    }
}
