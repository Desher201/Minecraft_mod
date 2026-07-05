package com.zapoc.horde;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;

public class HordeGroupAI {

    public static void tick(HordeGroup group) {

        Mob leader = group.getLeader();

        if (leader == null)
            return;

        if (!leader.isAlive())
            return;

        BlockPos target = group.getAttackPoint().getPosition();

        leader.getNavigation().moveTo(
                target.getX() + 0.5,
                target.getY(),
                target.getZ() + 0.5,
                1.0
        );

        for (Mob zombie : group.getZombies()) {

            if (zombie == leader)
                continue;

            if (!zombie.isAlive())
                continue;

            zombie.getNavigation().moveTo(
                    leader,
                    1.0
            );

        }

    }

}