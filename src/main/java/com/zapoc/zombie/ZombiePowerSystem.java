package com.zapoc.zombie;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;

public class ZombiePowerSystem {

    private static final double BASE = 1.015D;

    private static final double MAX_HEALTH_POWER = 6.0D;
    private static final double MAX_DAMAGE_POWER = 5.0D;

    public static double getPower(int day) {

        if (day < 1)
            day = 1;

        return Math.pow(BASE, day);
    }

    public static void applyToZombie(Zombie zombie, int day) {

        if (zombie == null)
            return;

        if (day < 1)
            day = 1;

        ZombieType type = ZombieTypeManager.getType(zombie);

        double healthPower = getPower(day);

        if (healthPower > MAX_HEALTH_POWER) {
            healthPower = MAX_HEALTH_POWER;
        }

        double damagePower = 1.0D + day * 0.025D;

        if (damagePower > MAX_DAMAGE_POWER) {
            damagePower = MAX_DAMAGE_POWER;
        }

        double maxHealth = getBaseHealth(type, day) * healthPower;
        double attackDamage = getBaseDamage(type, day) * damagePower;
        double movementSpeed = getMovementSpeed(type, day);

        if (zombie.getAttribute(Attributes.MAX_HEALTH) != null) {
            zombie.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
        }

        zombie.setHealth((float) maxHealth);

        if (zombie.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            zombie.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(attackDamage);
        }

        if (zombie.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
            zombie.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(movementSpeed);
        }
    }

    public static void applyToWorld(ServerLevel level, int day) {

        if (level == null)
            return;

        for (Zombie zombie : level.getEntitiesOfClass(
                Zombie.class,
                level.getWorldBorder().getCollisionShape().bounds()
        )) {

            if (zombie == null)
                continue;

            if (!zombie.isAlive())
                continue;

            applyToZombie(zombie, day);
        }

        System.out.println("[ZApoc] Zombies powered for day " + day);
    }

    private static double getBaseHealth(ZombieType type, int day) {

        return switch (type) {

            case RUNNER -> 16.0D;

            case TANK -> 55.0D;

            case HUNTER -> 24.0D;

            case BREAKER -> day >= 20 ? 35.0D : 22.0D;

            case NORMAL -> 20.0D;
        };
    }

    private static double getBaseDamage(ZombieType type, int day) {

        return switch (type) {

            case RUNNER -> 4.0D;

            case TANK -> 8.0D;

            case HUNTER -> 6.0D;

            case BREAKER -> day >= 20 ? 7.0D : 4.0D;

            case NORMAL -> 3.0D;
        };
    }

    private static double getMovementSpeed(ZombieType type, int day) {

        return switch (type) {

            case RUNNER -> getCappedSpeed(0.32D, day, 0.00035D, 0.38D);

            case TANK -> getCappedSpeed(0.17D, day, 0.00015D, 0.22D);

            case HUNTER -> getCappedSpeed(0.27D, day, 0.00025D, 0.33D);

            case BREAKER -> getCappedSpeed(0.22D, day, 0.00025D, 0.28D);

            case NORMAL -> getCappedSpeed(0.23D, day, 0.00020D, 0.29D);
        };
    }

    private static double getCappedSpeed(double baseSpeed, int day, double speedPerDay, double maxSpeed) {

        double speed = baseSpeed + day * speedPerDay;

        if (speed > maxSpeed)
            return maxSpeed;

        return speed;
    }
}