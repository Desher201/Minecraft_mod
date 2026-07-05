package com.zapoc.bed;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BedNoDropHelper {

    private static final int REMOVE_BLOCK_FLAGS = 35;
    private static final int CLEANUP_TIME_TICKS = 60;

    private static final Map<BlockPos, Long> CLEANUP_POSITIONS = new HashMap<>();

    public static void removeBedWithoutDrop(ServerLevel level, BlockPos bedPos) {

        if (level == null || bedPos == null)
            return;

        BlockState bedState = level.getBlockState(bedPos);

        rememberCleanupPosition(level, bedPos);

        if (!(bedState.getBlock() instanceof BedBlock)) {
            level.setBlock(bedPos, Blocks.AIR.defaultBlockState(), REMOVE_BLOCK_FLAGS);
            return;
        }

        BlockPos otherPartPos = getOtherBedPartPos(bedPos, bedState);
        BlockState otherState = level.getBlockState(otherPartPos);

        rememberCleanupPosition(level, otherPartPos);

        playBreakEffect(level, bedPos, bedState);

        if (otherState.getBlock() instanceof BedBlock) {
            playBreakEffect(level, otherPartPos, otherState);
        }

        level.setBlock(bedPos, Blocks.AIR.defaultBlockState(), REMOVE_BLOCK_FLAGS);

        if (otherState.getBlock() instanceof BedBlock) {
            level.setBlock(otherPartPos, Blocks.AIR.defaultBlockState(), REMOVE_BLOCK_FLAGS);
        }
    }

    public static BlockPos getOtherBedPartPos(BlockPos bedPos, BlockState bedState) {

        if (!(bedState.getBlock() instanceof BedBlock))
            return bedPos;

        Direction facing = bedState.getValue(BedBlock.FACING);
        BedPart part = bedState.getValue(BedBlock.PART);

        if (part == BedPart.HEAD) {
            return bedPos.relative(facing.getOpposite());
        }

        return bedPos.relative(facing);
    }

    public static boolean shouldRemoveBedDrop(ServerLevel level, BlockPos itemPos) {

        cleanupOldPositions(level);

        for (BlockPos pos : CLEANUP_POSITIONS.keySet()) {

            if (pos.distSqr(itemPos) <= 16.0D) {
                return true;
            }
        }

        return false;
    }

    private static void rememberCleanupPosition(ServerLevel level, BlockPos pos) {

        CLEANUP_POSITIONS.put(pos.immutable(), level.getGameTime() + CLEANUP_TIME_TICKS);
    }

    private static void cleanupOldPositions(ServerLevel level) {

        long gameTime = level.getGameTime();

        Iterator<Map.Entry<BlockPos, Long>> iterator = CLEANUP_POSITIONS.entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<BlockPos, Long> entry = iterator.next();

            if (entry.getValue() < gameTime) {
                iterator.remove();
            }
        }
    }

    private static void playBreakEffect(ServerLevel level, BlockPos pos, BlockState state) {

        level.levelEvent(
                2001,
                pos,
                Block.getId(state)
        );
    }
}
