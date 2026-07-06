package com.zapoc.ai;

import com.zapoc.bed.BedManager;
import com.zapoc.horde.HordeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class GoToBedGoal extends Goal {

    private static final double MOVE_SPEED = 1.0D;
    private static final double STOP_DISTANCE_SQR = 4.0D;
    private static final int PATH_RECALC_INTERVAL = 40;

    private final Mob mob;

    private BlockPos bedPos;
    private int pathRecalcTimer = 0;

    public GoToBedGoal(Mob mob) {

        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {

        if (mob == null)
            return false;

        if (!mob.isAlive())
            return false;

        if (mob.level.isClientSide())
            return false;

        if (HordeManager.isHordeActive())
            return false;

        LivingEntity target = mob.getTarget();

        if (target != null && target.isAlive())
            return false;

        if (!BedManager.hasBed())
            return false;

        if (!(mob.level instanceof ServerLevel level))
            return false;

        if (!level.dimension().equals(BedManager.getDimension()))
            return false;

        BlockPos pos = BedManager.getBedPos();

        if (pos == null)
            return false;

        bedPos = pos;

        return true;
    }

    @Override
    public boolean canContinueToUse() {

        if (mob == null)
            return false;

        if (!mob.isAlive())
            return false;

        if (mob.level.isClientSide())
            return false;

        if (HordeManager.isHordeActive())
            return false;

        LivingEntity target = mob.getTarget();

        if (target != null && target.isAlive())
            return false;

        if (!BedManager.hasBed())
            return false;

        if (!(mob.level instanceof ServerLevel level))
            return false;

        if (!level.dimension().equals(BedManager.getDimension()))
            return false;

        BlockPos pos = BedManager.getBedPos();

        if (pos == null)
            return false;

        bedPos = pos;

        return mob.distanceToSqr(
                bedPos.getX() + 0.5D,
                bedPos.getY(),
                bedPos.getZ() + 0.5D
        ) > STOP_DISTANCE_SQR;
    }

    @Override
    public void start() {

        pathRecalcTimer = 0;
        moveToBed();
    }

    @Override
    public void stop() {

        bedPos = null;
        pathRecalcTimer = 0;
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {

        if (bedPos == null)
            return;

        if (HordeManager.isHordeActive()) {
            mob.getNavigation().stop();
            return;
        }

        pathRecalcTimer--;

        if (pathRecalcTimer > 0)
            return;

        pathRecalcTimer = PATH_RECALC_INTERVAL;

        moveToBed();
    }

    private void moveToBed() {

        if (bedPos == null)
            return;

        mob.getNavigation().moveTo(
                bedPos.getX() + 0.5D,
                bedPos.getY(),
                bedPos.getZ() + 0.5D,
                MOVE_SPEED
        );
    }
}