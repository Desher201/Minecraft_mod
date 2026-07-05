package com.zapoc.zombie;

import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.MinecraftServer;

public class ZombiePowerSystem {

    // базовый множитель
    private static final double BASE = 1.015;

    /**
     * Получить силу мира по дню
     */
    public static double getPower(int day) {
        return Math.pow(BASE, day);
    }

    /**
     * Усилить одного зомби
     */
    public static void applyToZombie(Zombie zombie, int day) {

        double power = getPower(day);

        // HP
        double maxHp = zombie.getMaxHealth() * power;
        zombie.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH)
                .setBaseValue(maxHp);
        zombie.setHealth((float) maxHp);

        // урон
        zombie.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE)
                .setBaseValue(3.0 * power);

        // скорость
        zombie.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED)
                .setBaseValue(0.23 * power);
    }

    /**
     * Применить ко всем зомби в мире
     */
    public static void applyToWorld(ServerLevel level, int day) {

        for (Zombie zombie : level.getEntitiesOfClass(
                Zombie.class,
                level.getWorldBorder().getCollisionShape().bounds()
        )) {

        System.out.println("[ZApoc] Zombies powered for day " + day);
    }
}
}