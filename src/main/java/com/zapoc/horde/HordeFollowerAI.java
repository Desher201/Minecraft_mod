package com.zapoc.horde;

import com.zapoc.bed.BedManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class HordeFollowerAI {

    private static final double FOLLOW_DISTANCE = 4.0;
    private static final double MAX_DISTANCE_FROM_LEADER = 20.0;

    private static final double FOLLOW_SPEED = 1.1;
    private static final double ATTACK_SPEED = 1.15;

    private static final double BED_PRIORITY_RADIUS = 5.0;

    private static final int FOLLOW_PATH_RECALC_INTERVAL = 30;

    private static final Map<Integer, Long> NEXT_FOLLOW_PATH_RECALC_TICK = new HashMap<>();

    public static void tick(HordeGroup group) {

        if (group == null)
            return;

        if (!HordeManager.isHordeActive())
            return;

        Mob leader = group.getLeader();

        if (leader == null)
            return;

        if (!leader.isAlive())
            return;

        if (!(leader.level instanceof ServerLevel level))
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

            if (zombie.isRemoved())
                continue;

            double distanceToLeaderSqr = zombie.distanceToSqr(leader);

            if (bedPriority) {

                zombie.setTarget(null);

                if (distanceToLeaderSqr > FOLLOW_DISTANCE * FOLLOW_DISTANCE) {
                    moveToTarget(level, zombie, leader, FOLLOW_SPEED);
                } else {
                    zombie.getNavigation().stop();
                }

                continue;
            }

            if (leaderTarget != null && leaderTarget.isAlive()) {

                zombie.setTarget(leaderTarget);

                if (distanceToLeaderSqr <= MAX_DISTANCE_FROM_LEADER * MAX_DISTANCE_FROM_LEADER) {
                    moveToTarget(level, zombie, leaderTarget, ATTACK_SPEED);
                } else {
                    moveToTarget(level, zombie, leader, FOLLOW_SPEED);
                }

                continue;
            }

            zombie.setTarget(null);

            if (distanceToLeaderSqr > FOLLOW_DISTANCE * FOLLOW_DISTANCE) {
                moveToTarget(level, zombie, leader, FOLLOW_SPEED);
            } else {
                zombie.getNavigation().stop();
            }
        }
    }

    private static void moveToTarget(ServerLevel level, Mob mob, LivingEntity target, double speed) {

        long gameTime = level.getGameTime();
        int mobId = mob.getId();
        long nextTick = NEXT_FOLLOW_PATH_RECALC_TICK.getOrDefault(mobId, 0L);

        if (gameTime < nextTick)
            return;

        NEXT_FOLLOW_PATH_RECALC_TICK.put(
                mobId,
                gameTime + FOLLOW_PATH_RECALC_INTERVAL + Math.abs(mobId % 10)
        );

        mob.getNavigation().moveTo(target, speed);
    }

    public static void reset() {
        NEXT_FOLLOW_PATH_RECALC_TICK.clear();
    }
}
