package com.zapoc.bed;

import com.zapoc.config.ZapocConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class BedChunkLoader {

    public static void loadChunks(ServerLevel level) {

        if (!BedManager.hasBed())
            return;

        BlockPos pos = BedManager.getBedPos();
        int radius = getChunkRadius();

        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {

                level.setChunkForced(
                        chunkX + x,
                        chunkZ + z,
                        true
                );
            }
        }

        int size = radius * 2 + 1;
        System.out.println("[ZApoc] Loaded " + size + "x" + size + " chunks around global bed.");
    }

    public static void unloadChunks(ServerLevel level) {

        if (BedManager.getBedPos() == null)
            return;

        BlockPos pos = BedManager.getBedPos();
        int radius = getChunkRadius();

        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {

                level.setChunkForced(
                        chunkX + x,
                        chunkZ + z,
                        false
                );
            }
        }

        System.out.println("[ZApoc] Unloaded global bed chunks.");
    }

    public static int getChunkRadius() {
        return ZapocConfig.BED_CHUNK_LOAD_RADIUS.get();
    }
}
