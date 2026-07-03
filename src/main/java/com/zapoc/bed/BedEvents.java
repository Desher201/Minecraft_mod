package com.zapoc.bed;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class BedEvents {

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {

        if (!(event.getWorld() instanceof Level level))
            return;

        BlockState state = event.getState();

        if (!(state.getBlock() instanceof BedBlock))
            return;

        BedManager.setBed(event.getPos(), level.dimension());

        System.out.println("[ZApoc] Global bed placed at " + event.getPos());
    }
}