package com.zapoc.bed;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BedEvents {

    /**
     * Игрок поставил кровать.
     */
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {

        BlockState state = event.getPlacedBlock();

        if (!(state.getBlock() instanceof BedBlock)) {
            return;
        }

        BlockPos pos = event.getPos();

        BedManager.setBed(pos);

        System.out.println("[ZApoc] Global bed placed at " + pos);
    }

    /**
     * Кровать сломали.
     */
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {

        if (!BedManager.hasBed()) {
            return;
        }

        BlockPos pos = event.getPos();

        if (!pos.equals(BedManager.getBedPos())) {
            return;
        }

        BedManager.removeBed();

        System.out.println("[ZApoc] Global bed destroyed!");
    }
}
