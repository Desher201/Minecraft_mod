package com.zapoc.bed;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class BedManager {

    // 📍 координаты общей кровати
    private static BlockPos bedPos = null;

    // 🌍 измерение кровати
    private static ResourceKey<Level> dimension = null;

    // 💀 режим хардкора
    private static boolean hardcore = true;

    // =========================
    // 🔍 CHECKS
    // =========================

    public static boolean hasBed() {
        return bedPos != null && dimension != null;
    }

    public static boolean isHardcore() {
        return hardcore;
    }

    // =========================
    // 📍 GETTERS
    // =========================

    public static BlockPos getBedPos() {
        return bedPos;
    }

    public static ResourceKey<Level> getDimension() {
        return dimension;
    }

    // =========================
    // 🛏 SET BED
    // =========================

    public static void setBed(BlockPos pos, ResourceKey<Level> dim) {

        bedPos = pos;
        dimension = dim;

        hardcore = false;

        System.out.println("[ZApoc] Global bed placed at " + pos + " in " + dim.location());
    }

    // =========================
    // 💥 REMOVE BED
    // =========================

    public static void removeBed() {

        bedPos = null;
        dimension = null;

        hardcore = true;

        System.out.println("[ZApoc] Bed destroyed. HARDCORE enabled.");
    }

    // =========================
    // ⚙ FORCE SET MODE
    // =========================

    public static void setHardcore(boolean value) {
        hardcore = value;
    }

    // =========================
    // 🔄 RESET SYSTEM (optional debug)
    // =========================

    public static void reset() {

        bedPos = null;
        dimension = null;
        hardcore = true;

        System.out.println("[ZApoc] Bed system reset.");
    }
}
