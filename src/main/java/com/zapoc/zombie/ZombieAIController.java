package com.zapoc.zombie;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;

public class ZombieAIController {

    public static void applyAI(Zombie zombie, ZombieType type) {

        switch (type) {

            case RUNNER -> {
                zombie.getAttribute(Attributes.MOVEMENT_SPEED)
                        .setBaseValue(0.35);
            }

            case TANK -> {
                zombie.getAttribute(Attributes.MAX_HEALTH)
                        .setBaseValue(60);
                zombie.setHealth(60f);
            }

            case HUNTER -> {
                zombie.getAttribute(Attributes.FOLLOW_RANGE)
                        .setBaseValue(64.0);
            }

            case BREAKER -> {
                zombie.setCanBreakDoors(true);
                zombie.getAttribute(Attributes.FOLLOW_RANGE)
                        .setBaseValue(48.0);
            }

            default -> {
                // NORMAL
            }
        }
    }
}