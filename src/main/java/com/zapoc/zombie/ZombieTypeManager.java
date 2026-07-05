package com.zapoc.zombie;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.monster.Zombie;

public class ZombieTypeManager {

    private static final String TAG = "ZapocZombieType";

    /**
     * Сохранить тип зомби
     */
    public static void setType(Zombie zombie, ZombieType type) {

        CompoundTag tag = zombie.getPersistentData();

        tag.putString(TAG, type.name());
    }

    /**
     * Получить тип
     */
    public static ZombieType getType(Zombie zombie) {

        CompoundTag tag = zombie.getPersistentData();

        if (!tag.contains(TAG))
            return ZombieType.NORMAL;

        try {
            return ZombieType.valueOf(tag.getString(TAG));
        } catch (Exception e) {
            return ZombieType.NORMAL;
        }
    }
}
