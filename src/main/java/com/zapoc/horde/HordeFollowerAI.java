package com.zapoc.horde;

import net.minecraft.world.entity.Mob;

public class HordeFollowerAI {

    private static final double FOLLOW_DISTANCE = 6.0;
    private static final double SPEED = 1.1;

    public static void tick(HordeGroup group) {

        Mob leader = group.getLeader();

        if (leader == null)
            return;

        if (!leader.isAlive())
            return;

        for (Mob zombie : group.getZombies()) {

            if (zombie == leader)
                continue;

            if (!zombie.isAlive())
                continue;

            double distance = zombie.distanceTo(leader);

            if (distance > FOLLOW_DISTANCE) {

                zombie.getNavigation().moveTo(
                        leader,
                        SPEED
                );

            }

            // Если лидер атакует игрока,
            // вся группа переключается на ту же цель.
            if (leader.getTarget() != null) {

                zombie.setTarget(leader.getTarget());

            }

        }

    }

}