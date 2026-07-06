package com.zapoc.zombie;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;

public class ZombieTypeApplier {

    public static void apply(Zombie zombie, ZombieType type) {
        apply(zombie, type, 1);
    }

    public static void apply(Zombie zombie, ZombieType type, int day) {

        switch (type) {

            case NORMAL -> {
            }

            case RUNNER -> {

                zombie.getAttribute(Attributes.MOVEMENT_SPEED)
                        .setBaseValue(0.35D);

                zombie.getAttribute(Attributes.MAX_HEALTH)
                        .setBaseValue(16D);

                zombie.setHealth(16F);
            }

            case TANK -> {

                zombie.getAttribute(Attributes.MAX_HEALTH)
                        .setBaseValue(50D);

                zombie.setHealth(50F);

                zombie.getAttribute(Attributes.ATTACK_DAMAGE)
                        .setBaseValue(8D);

                zombie.getAttribute(Attributes.MOVEMENT_SPEED)
                        .setBaseValue(0.18D);
            }

            case HUNTER -> {

                zombie.getAttribute(Attributes.MOVEMENT_SPEED)
                        .setBaseValue(0.28D);

                zombie.getAttribute(Attributes.ATTACK_DAMAGE)
                        .setBaseValue(6D);

                zombie.getAttribute(Attributes.FOLLOW_RANGE)
                        .setBaseValue(48D);
            }

            case BREAKER -> {

                if (day >= 20) {

                    zombie.getAttribute(Attributes.MAX_HEALTH)
                            .setBaseValue(35D);

                    zombie.setHealth(35F);

                    zombie.getAttribute(Attributes.ATTACK_DAMAGE)
                            .setBaseValue(7D);
                }
            }

            case CRAWLER -> {

                zombie.getAttribute(Attributes.MAX_HEALTH)
                        .setBaseValue(14D);

                zombie.setHealth(14F);

                zombie.getAttribute(Attributes.ATTACK_DAMAGE)
                        .setBaseValue(4D);

                zombie.getAttribute(Attributes.MOVEMENT_SPEED)
                        .setBaseValue(0.24D);

                zombie.getAttribute(Attributes.FOLLOW_RANGE)
                        .setBaseValue(40D);
            }
        }
    }
}
