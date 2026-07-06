package com.zapoc.horde;

import com.zapoc.bed.BedManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;

import java.util.HashMap;
import java.util.Map;

public class HordeLeaderAI {

    private static final double LEADER_MOVE_SPEED = 1.05D;
    private static final double STOP_DISTANCE_SQR = 4.0D;
    private static final int PATH_RECALC_INTERVAL = 40;

    private static final Map<Integer, Long> NEXT_PATH_RECALC_TICK = new HashMap<>();

    public static void tick(HordeGroup group) {

        if (group == null)
            return;

        if (!HordeManager.isHordeActive())
            return;

        if (!BedManager.hasBed())
            return;

        Mob leader = group.getLeader();

        if (leader == null)
            return;

        if (!leader.isAlive())
            return;

        if (!(leader.level instanceof ServerLevel level))
            return;

        if (!level.dimension().equals(BedManager.getDimension()))
            return;

        BlockPos bedPos = BedManager.getBedPos();

        if (bedPos == null)
            return;

        leader.setTarget(null);

        if (HordeBedAttackAI.tick(group))
            return;

        moveLeaderToBed(level, group, leader, bedPos);
    }

    private static void moveLeaderToBed(ServerLevel level, HordeGroup group, Mob leader, BlockPos bedPos) {

        double distanceToBed = leader.distanceToSqr(
                bedPos.getX() + 0.5D,
                bedPos.getY(),
                bedPos.getZ() + 0.5D
        );

        if (distanceToBed <= STOP_DISTANCE_SQR) {
            leader.getNavigation().stop();
            return;
        }

        long gameTime = level.getGameTime();
        long nextTick = NEXT_PATH_RECALC_TICK.getOrDefault(group.getId(), 0L);

        if (gameTime < nextTick)
            return;

        NEXT_PATH_RECALC_TICK.put(
                group.getId(),
                gameTime + PATH_RECALC_INTERVAL + Math.abs(group.getId() % 10)
        );

        leader.getNavigation().moveTo(
                bedPos.getX() + 0.5D,
                bedPos.getY(),
                bedPos.getZ() + 0.5D,
                LEADER_MOVE_SPEED
        );
    }

    public static void reset() {
        NEXT_PATH_RECALC_TICK.clear();
    }
}
