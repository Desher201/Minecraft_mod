package com.zapoc.zombie;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;

public class ZombieTypeManager {

    private static final String TAG = "ZapocZombieType";

    public static void setType(Mob mob, ZombieType type) {

        if (mob == null || type == null)
            return;

        CompoundTag tag = mob.getPersistentData();

        tag.putString(TAG, type.name());
    }

    public static ZombieType getType(Mob mob) {

        if (mob == null)
            return ZombieType.NORMAL;

        CompoundTag tag = mob.getPersistentData();

        if (!tag.contains(TAG))
            return ZombieType.NORMAL;

        try {

            return ZombieType.valueOf(tag.getString(TAG));

        } catch (Exception e) {

            return ZombieType.NORMAL;
        }
    }
}