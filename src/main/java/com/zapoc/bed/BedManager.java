package com.zapoc.bed;

import net.minecraft.core.BlockPos;

public class BedManager {

    /**
     * Координаты общей кровати.
     */
    private static BlockPos bedPos = null;

    /**
     * Активен ли сейчас режим хардкора.
     */
    private static boolean hardcore = true;

    /**
     * Есть ли вообще установленная кровать.
     */
    public static boolean hasBed() {
        return bedPos != null;
    }

    /**
     * Координаты кровати.
     */
    public static BlockPos getBedPos() {
        return bedPos;
    }

    /**
     * Установить новую кровать.
     */
    public static void setBed(BlockPos pos) {

        bedPos = pos;

        hardcore = false;

        System.out.println("[ZApoc] Bed registered at " + pos);

    }

    /**
     * Удалить кровать.
     */
    public static void removeBed() {

        bedPos = null;

        hardcore = true;

        System.out.println("[ZApoc] Bed destroyed.");

    }

    /**
     * Проверка режима.
     */
    public static boolean isHardcore() {
        return hardcore;
    }

    /**
     * Принудительное изменение режима.
     */
    public static void setHardcore(boolean value) {
        hardcore = value;
    }

}
