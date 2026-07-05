package com.zapoc.zombie;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ZombieSpawnEvents {

    @SubscribeEvent
    public static void onZombieSpawn(LivingSpawnEvent.SpecialSpawn event) {

        if (!(event.getEntity() instanceof Zombie zombie))
            return;

        if (!(event.getWorld() instanceof ServerLevel level))
            return;

        int day = (int) (level.getDayTime() / 24000L) + 1;

        // Выбираем тип зомби
        ZombieType type = ZombieTypeSelector.getType(day);

        // Сохраняем тип
        ZombieTypeManager.setType(zombie, type);

        // Применяем характеристики типа
        ZombieTypeApplier.apply(zombie, type);

        // Усиливаем по текущему дню
        ZombiePowerSystem.applyToZombie(zombie, day);

        // Имя
        zombie.setCustomName(
                new TextComponent("§c" + type.name())
        );

        zombie.setCustomNameVisible(true);

        // Особое AI
        ZombieAIController.applyAI(zombie, type);
    }

}