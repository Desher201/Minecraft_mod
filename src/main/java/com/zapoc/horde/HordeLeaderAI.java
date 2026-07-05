package com.zapoc.horde;

import com.zapoc.bed.BedManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class HordeLeaderAI {

    private static final double SPEED_TO_BED = 1.0;
    private static final double SPEED_TO_PLAYER = 1.15;

    private static final double PLAYER_DETECT_RADIUS = 14.0;
    private static final double PLAYER_FORGET_RADIUS = 28.0;

    private static final double BED_PRIORITY_RADIUS = 5.0;

    public static void tick(HordeGroup group) {

        if (group == null)
            return;

        Mob leader = group.getLeader();

        if (leader == null)
            return;

        if (!leader.isAlive())
            return;

        if (!BedManager.hasBed())
            return;

        BlockPos bedPos = BedManager.getBedPos();

        if (bedPos == null)
            return;

        double distanceToBedSqr = leader.distanceToSqr(
                Vec3.atCenterOf(bedPos)
        );

        if (distanceToBedSqr <= BED_PRIORITY_RADIUS * BED_PRIORITY_RADIUS) {

            leader.setTarget(null);

            leader.getNavigation().moveTo(
                    bedPos.getX() + 0.5,
                    bedPos.getY(),
                    bedPos.getZ() + 0.5,
                    SPEED_TO_BED
            );

            return;
        }

        LivingEntity currentTarget = leader.getTarget();

        if (currentTarget != null) {

            if (!currentTarget.isAlive()
                    || leader.distanceToSqr(currentTarget) > PLAYER_FORGET_RADIUS * PLAYER_FORGET_RADIUS) {

                leader.setTarget(null);

            } else {

                leader.getNavigation().moveTo(currentTarget, SPEED_TO_PLAYER);
                return;
            }
        }

        Player nearbyPlayer = leader.level.getNearestPlayer(
                leader,
                PLAYER_DETECT_RADIUS
        );

        if (nearbyPlayer != null && leader.hasLineOfSight(nearbyPlayer)) {

            leader.setTarget(nearbyPlayer);
            HordeGroupManager.alertGroup(leader, nearbyPlayer);
            leader.getNavigation().moveTo(nearbyPlayer, SPEED_TO_PLAYER);
            return;
        }

        leader.setTarget(null);

        leader.getNavigation().moveTo(
                bedPos.getX() + 0.5,
                bedPos.getY(),
                bedPos.getZ() + 0.5,
                SPEED_TO_BED
        );
    }
}