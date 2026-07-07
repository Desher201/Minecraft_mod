package com.zapoc.world;

import com.zapoc.config.ZapocConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class DrownedBlockEvents {

    @SubscribeEvent
    public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {

        if (!ZapocConfig.BLOCK_DROWNED_SPAWNS.get())
            return;

        if (event.getEntity().getType() == EntityType.DROWNED) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {

        if (!ZapocConfig.BLOCK_DROWNED_SPAWNS.get())
            return;

        if (event.getWorld().isClientSide())
            return;

        if (event.getEntity().getType() == EntityType.DROWNED) {
            event.setCanceled(true);
            event.getEntity().discard();
        }
    }

    @SubscribeEvent
    public static void onLivingConversion(LivingConversionEvent.Pre event) {

        if (!ZapocConfig.BLOCK_DROWNED_SPAWNS.get())
            return;

        if (event.getOutcome() == EntityType.DROWNED) {
            event.setCanceled(true);
        }
    }
}
