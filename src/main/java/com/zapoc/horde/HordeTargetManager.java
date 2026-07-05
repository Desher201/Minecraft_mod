package com.zapoc.horde;

import com.zapoc.bed.BedManager;
import net.minecraft.core.BlockPos;

public class HordeTargetManager {

    /**
     * Возвращает цель орды (общую кровать).
     */
    public static BlockPos getTarget() {

        if (!BedManager.hasBed())
            return null;

        return BedManager.getBedPos();
    }

    /**
     * Есть ли сейчас цель.
     */
    public static boolean hasTarget() {

        return BedManager.hasBed();

    }

}