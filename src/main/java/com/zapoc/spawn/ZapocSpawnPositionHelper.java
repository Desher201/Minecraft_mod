package com.zapoc.spawn;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Optional;
import java.util.Random;

public class ZapocSpawnPositionHelper {

    private static final int DEFAULT_ATTEMPTS = 32;

    public static Optional<BlockPos> findSurfaceSpawnPosition(
            ServerLevel level,
            BlockPos center,
            int minDistance,
            int maxDistance,
            Random random
    ) {

        int safeMin = Math.max(0, minDistance);
        int safeMax = Math.max(safeMin, maxDistance);

        for (int attempt = 0; attempt < DEFAULT_ATTEMPTS; attempt++) {

            double angle = random.nextDouble() * Math.PI * 2.0D;
            int distance = safeMin;

            if (safeMax > safeMin) {
                distance += random.nextInt(safeMax - safeMin + 1);
            }

            int x = center.getX() + (int) Math.round(Math.cos(angle) * distance);
            int z = center.getZ() + (int) Math.round(Math.sin(angle) * distance);
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos pos = new BlockPos(x, y, z);

            if (isValidSpawnPosition(level, pos)) {
                return Optional.of(pos);
            }
        }

        return Optional.empty();
    }

    public static Optional<BlockPos> findSurfaceSpawnPositionNear(
            ServerLevel level,
            BlockPos center,
            int radius,
            Random random
    ) {

        int safeRadius = Math.max(0, radius);

        for (int attempt = 0; attempt < DEFAULT_ATTEMPTS; attempt++) {

            int x = center.getX();
            int z = center.getZ();

            if (safeRadius > 0) {
                x += random.nextInt(safeRadius * 2 + 1) - safeRadius;
                z += random.nextInt(safeRadius * 2 + 1) - safeRadius;
            }

            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos pos = new BlockPos(x, y, z);

            if (isValidSpawnPosition(level, pos)) {
                return Optional.of(pos);
            }
        }

        return Optional.empty();
    }

    public static boolean isValidSpawnPosition(ServerLevel level, BlockPos pos) {

        BlockState below = level.getBlockState(pos.below());
        BlockState feet = level.getBlockState(pos);
        BlockState head = level.getBlockState(pos.above());

        if (!feet.isAir())
            return false;

        if (!head.isAir())
            return false;

        if (!level.getFluidState(pos).isEmpty())
            return false;

        if (!level.getFluidState(pos.above()).isEmpty())
            return false;

        if (!below.getFluidState().isEmpty())
            return false;

        if (!below.getMaterial().isSolid())
            return false;

        return below.isValidSpawn(level, pos.below(), EntityType.ZOMBIE);
    }
}
