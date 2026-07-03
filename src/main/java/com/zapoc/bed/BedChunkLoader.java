package com.zapoc.bed;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class BedChunkLoader {

    // Радиус загрузки (1 = 3×3 чанка)
    private static final int CHUNK_RADIUS = 1;

    /**
     * Загружает чанки вокруг общей кровати.
     */
    public static void loadChunks(ServerLevel level) {

        if (!BedManager.hasBed())
            return;

        BlockPos pos = BedManager.getBedPos();

        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;

        for (int x = -CHUNK_RADIUS; x <= CHUNK_RADIUS; x++) {
            for (int z = -CHUNK_RADIUS; z <= CHUNK_RADIUS; z++) {

                level.setChunkForced(
                        chunkX + x,
                        chunkZ + z,
                        true
                );
            }
        }

        System.out.println("[ZApoc] Loaded 3x3 chunks around global bed.");
    }

    /**
     * Выгружает чанки.
     */
    public static void unloadChunks(ServerLevel level) {

        if (BedManager.getBedPos() == null)
            return;

        BlockPos pos = BedManager.getBedPos();

        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;

        for (int x = -CHUNK_RADIUS; x <= CHUNK_RADIUS; x++) {
            for (int z = -CHUNK_RADIUS; z <= CHUNK_RADIUS; z++) {

                level.setChunkForced(
                        chunkX + x,
                        chunkZ + z,
                        false
                );
            }
        }

        System.out.println("[ZApoc] Unloaded global bed chunks.");
    }
}
