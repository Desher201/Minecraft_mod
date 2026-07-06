package com.zapoc.zombie;

import com.zapoc.config.ZapocConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;

public class ZombiePowerSystem {

    public static double getPower(int day) {

        if (day < 1)
            day = 1;

        return Math.pow(ZapocConfig.HEALTH_POWER_BASE.get(), day);
    }

    public static void applyToZombie(Zombie zombie, int day) {

        if (zombie == null)
            return;

        if (day < 1)
            day = 1;

        ZombieType type = ZombieTypeManager.getType(zombie);

        double healthPower = getPower(day);

        double maxHealthPower = ZapocConfig.MAX_HEALTH_POWER.get();

        if (healthPower > maxHealthPower) {
            healthPower = maxHealthPower;
        }

        double damagePower = 1.0D + day * ZapocConfig.DAMAGE_PER_DAY.get();
        double maxDamagePower = ZapocConfig.MAX_DAMAGE_POWER.get();

        if (damagePower > maxDamagePower) {
            damagePower = maxDamagePower;
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

            case CRAWLER -> 14.0D;

            case NORMAL -> 20.0D;
        };
    }

    private static double getBaseDamage(ZombieType type, int day) {

        return switch (type) {

            case RUNNER -> 4.0D;

            case TANK -> 8.0D;

            case HUNTER -> 6.0D;

            case BREAKER -> day >= 20 ? 7.0D : 4.0D;

            case CRAWLER -> 4.0D;

            case NORMAL -> 3.0D;
        };
    }

    private static double getMovementSpeed(ZombieType type, int day) {

        return switch (type) {

            case RUNNER -> getCappedSpeed(
                    ZapocConfig.RUNNER_BASE_SPEED.get(),
                    day,
                    0.00035D,
                    ZapocConfig.RUNNER_MAX_SPEED.get()
            );

            case TANK -> getCappedSpeed(
                    ZapocConfig.TANK_BASE_SPEED.get(),
                    day,
                    0.00015D,
                    ZapocConfig.TANK_MAX_SPEED.get()
            );

            case HUNTER -> getCappedSpeed(
                    ZapocConfig.HUNTER_BASE_SPEED.get(),
                    day,
                    0.00025D,
                    ZapocConfig.HUNTER_MAX_SPEED.get()
            );

            case BREAKER -> getCappedSpeed(
                    ZapocConfig.BREAKER_BASE_SPEED.get(),
                    day,
                    0.00025D,
                    ZapocConfig.BREAKER_MAX_SPEED.get()
            );

            case CRAWLER -> getCappedSpeed(
                    ZapocConfig.CRAWLER_BASE_SPEED.get(),
                    day,
                    0.00020D,
                    ZapocConfig.CRAWLER_MAX_SPEED.get()
            );

            case NORMAL -> getCappedSpeed(
                    ZapocConfig.NORMAL_BASE_SPEED.get(),
                    day,
                    0.00020D,
                    ZapocConfig.NORMAL_MAX_SPEED.get()
            );
        };
    }

    private static double getCappedSpeed(double baseSpeed, int day, double speedPerDay, double maxSpeed) {

        double speed = baseSpeed + day * speedPerDay;

        if (speed > maxSpeed)
            return maxSpeed;

        return speed;
    }
}
