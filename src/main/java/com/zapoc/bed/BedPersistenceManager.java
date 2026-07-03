package com.zapoc.bed;

import net.minecraft.server.level.ServerLevel;

public class BedPersistenceManager {

    /**
     * Сохранить состояние кровати.
     */
    public static void saveBed(ServerLevel level) {

        BedSavedData data = BedSavedData.get(level);

        if (BedManager.hasBed()) {

            data.setBed(
                    BedManager.getBedPos(),
                    BedManager.getDimension()
            );

            System.out.println("[ZApoc] Bed saved.");

        } else {

            data.removeBed();

            System.out.println("[ZApoc] Bed removed from save.");
        }
    }

    /**
     * Загрузить состояние кровати.
     */
    public static void loadBed(ServerLevel level) {

        BedSavedData data = BedSavedData.get(level);

        // Если сохранённой кровати нет
        if (data.getBedPos() == null) {

            BedManager.reset();

            System.out.println("[ZApoc] No saved bed.");

            return;
        }

        // Восстанавливаем BedManager
        BedManager.setBed(
                data.getBedPos(),
                data.getDimension()
        );

        BedManager.setHardcore(
                data.isHardcore()
        );

        System.out.println("[ZApoc] Bed restored.");

        // Получаем нужное измерение
        ServerLevel bedLevel = level.getServer().getLevel(data.getDimension());

        if (bedLevel != null) {

            // Загружаем чанки
            BedChunkLoader.loadChunks(bedLevel);

            // Восстанавливаем общий респавн
            BedSpawnManager.updateAllPlayers(
                    bedLevel.getServer()
            );

            System.out.println("[ZApoc] Global spawn restored.");
        }
    }
}