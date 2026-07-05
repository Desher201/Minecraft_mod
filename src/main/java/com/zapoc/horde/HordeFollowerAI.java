package com.zapoc.horde;

import com.zapoc.bed.BedManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public class HordeFollowerAI {

    private static final double FOLLOW_DISTANCE = 4.0;
    private static final double MAX_DISTANCE_FROM_LEADER = 20.0;

    private static final double FOLLOW_SPEED = 1.1;
    private static final double ATTACK_SPEED = 1.15;

    private static final double BED_PRIORITY_RADIUS = 5.0;

    public static void tick(HordeGroup group) {

        if (group == null)
            return;

        Mob leader = group.getLeader();

        if (leader == null)
            return;

        if (!leader.isAlive())
            return;

        boolean bedPriority = false;

        if (BedManager.hasBed()) {

            BlockPos bedPos = BedManager.getBedPos();

            if (bedPos != null) {

                double leaderDistanceToBedSqr = leader.distanceToSqr(
                        Vec3.atCenterOf(bedPos)
                );

                bedPriority = leaderDistanceToBedSqr <= BED_PRIORITY_RADIUS * BED_PRIORITY_RADIUS;
            }
        }

        LivingEntity leaderTarget = leader.getTarget();

        for (Mob zombie : group.getZombies()) {

            if (zombie == null)
                continue;

            if (zombie == leader)
                continue;

            if (!zombie.isAlive())
                continue;

            double distanceToLeader = zombie.distanceTo(leader);

            if (bedPriority) {

                zombie.setTarget(null);

                if (distanceToLeader > FOLLOW_DISTANCE) {
                    zombie.getNavigation().moveTo(leader, FOLLOW_SPEED);
                }

                continue;
            }

            if (leaderTarget != null && leaderTarget.isAlive()) {

                zombie.setTarget(leaderTarget);

                if (distanceToLeader <= MAX_DISTANCE_FROM_LEADER) {
                    zombie.getNavigation().moveTo(leaderTarget, ATTACK_SPEED);
                } else {
                    zombie.getNavigation().moveTo(leader, FOLLOW_SPEED);
                }

                continue;
            }

            zombie.setTarget(null);

            if (distanceToLeader > FOLLOW_DISTANCE) {
                zombie.getNavigation().moveTo(leader, FOLLOW_SPEED);
            }
        }
    }
}