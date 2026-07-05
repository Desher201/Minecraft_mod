package com.zapoc.zombie;

import com.zapoc.horde.HordeBlockBreakAI;
import net.minecraft.world.entity.Mob;

public class ZombieRoleAI {

    public static void tick(Mob mob) {

        if (mob == null)
            return;

        if (!mob.isAlive())
            return;

        ZombieType type = ZombieTypeManager.getType(mob);

        switch (type) {

            case BREAKER -> HordeBlockBreakAI.tickSingle(mob);

            case RUNNER -> {
                // Later: runner logic
            }

            case TANK -> {
                // Later: tank logic
            }

            case HUNTER -> {
                // Later: hunter logic
            }

            case NORMAL -> {
                // Normal zombie has no special role logic
            }
        }
    }
}