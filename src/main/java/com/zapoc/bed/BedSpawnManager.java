package com.zapoc.bed;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class BedSpawnManager {

    /**
     * Установить общую точку возрождения всем игрокам.
     */
    public static void updateAllPlayers(MinecraftServer server) {

        if (!BedManager.hasBed())
            return;

        BlockPos pos = BedManager.getBedPos();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {

            player.setRespawnPosition(
                    BedManager.getDimension(), // измерение
                    pos,                       // координаты
                    0f,                        // угол
                    true,                      // forced
                    false                      // sendMessage
            );

            System.out.println("[ZApoc] Respawn updated for " + player.getName().getString());
        }
    }
}
