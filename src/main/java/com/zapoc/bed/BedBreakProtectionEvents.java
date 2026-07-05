package com.zapoc.bed;

import com.zapoc.horde.HordeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;

@Mod.EventBusSubscriber
public class BedBreakProtectionEvents {

    @SubscribeEvent
    public static void onPlayerBreakBlock(BlockEvent.BreakEvent event) {

        LevelAccessor world = event.getWorld();

        if (!(world instanceof ServerLevel level))
            return;

        BlockState state = event.getState();

        if (!(state.getBlock() instanceof BedBlock))
            return;

        BlockPos pos = event.getPos();

        if (!isGlobalBedPart(level, pos))
            return;

        event.setCanceled(true);

        if (HordeManager.isHordeActive()) {

            if (event.getPlayer() instanceof ServerPlayer player) {
                player.displayClientMessage(
                        new TextComponent("You cannot break the base bed during horde night."),
                        true
                );
            }

            return;
        }

        removeGlobalBed(level, pos);
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {

        if (!(event.getWorld() instanceof ServerLevel level))
            return;

        if (!BedManager.hasBed())
            return;

        boolean containsGlobalBed = false;

        Iterator<BlockPos> iterator = event.getAffectedBlocks().iterator();

        while (iterator.hasNext()) {

            BlockPos pos = iterator.next();

            if (isGlobalBedPart(level, pos)) {
                containsGlobalBed = true;
                iterator.remove();
            }
        }

        if (!containsGlobalBed)
            return;

        removeGlobalBed(level, BedManager.getBedPos());
    }

    private static void removeGlobalBed(ServerLevel level, BlockPos pos) {

        BedNoDropHelper.removeBedWithoutDrop(level, pos);

        BedChunkLoader.unloadChunks(level);
        BedManager.removeBed();
        BedManager.setHardcore(true);
        BedPersistenceManager.saveBed(level);

        System.out.println("[ZApoc] Global bed was removed without drop.");
    }

    private static boolean isGlobalBedPart(ServerLevel level, BlockPos pos) {

        if (!BedManager.hasBed())
            return false;

        BlockPos bedPos = BedManager.getBedPos();

        if (bedPos == null)
            return false;

        if (pos.equals(bedPos))
            return true;

        BlockState savedState = level.getBlockState(bedPos);

        if (savedState.getBlock() instanceof BedBlock) {

            BlockPos otherPart = BedNoDropHelper.getOtherBedPartPos(bedPos, savedState);

            return pos.equals(otherPart);
        }

        BlockState currentState = level.getBlockState(pos);

        if (currentState.getBlock() instanceof BedBlock) {

            BlockPos otherPart = BedNoDropHelper.getOtherBedPartPos(pos, currentState);

            return otherPart.equals(bedPos);
        }

        return false;
    }
}
