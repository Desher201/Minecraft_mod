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

        if (type != ZombieType.NORMAL && (mob.tickCount + mob.getId()) % 10 == 0) {
            ZombiePositioningHelper.applySoftSeparation(mob);
        }

        switch (type) {

            case BREAKER -> HordeBlockBreakAI.tickSingle(mob);

            case CRAWLER -> CrawlerWallClimbAI.tick(mob);

            case RUNNER -> RunnerRoleAI.tick(mob);

            case TANK -> TankRoleAI.tick(mob);

            case HUNTER -> HunterRoleAI.tick(mob);

            case NORMAL -> {
            }
        }
    }
}
