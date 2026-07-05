package com.zapoc.ai;

import com.zapoc.bed.BedManager;
import com.zapoc.horde.HordeGroupManager;
import com.zapoc.horde.HordeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GoToBedGoal extends Goal {

    private static final double SPEED = 1.0;
    private static final double ARRIVE_DIST_SQR = 4.0;

    private final Mob mob;

    private BlockPos targetBed;
    private int repathCooldown = 0;

    public GoToBedGoal(Mob mob) {

        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {

        Level level = mob.level;

        if (level.isClientSide())
            return false;

        if (!HordeManager.isHordeActive())
            return false;

        if (!BedManager.hasBed())
            return false;

        if (HordeGroupManager.isTracked(mob))
            return false;

        targetBed = BedManager.getBedPos();

        return targetBed != null;
    }

    @Override
    public boolean canContinueToUse() {

        if (!HordeManager.isHordeActive())
            return false;

        if (!BedManager.hasBed())
            return false;

        if (HordeGroupManager.isTracked(mob))
            return false;

        if (targetBed == null)
            return false;

        return mob.distanceToSqr(Vec3.atCenterOf(targetBed)) > ARRIVE_DIST_SQR;
    }

    @Override
    public void start() {

        moveToBed();
    }

    @Override
    public void tick() {

        if (targetBed == null)
            return;

        repathCooldown--;

        if (repathCooldown <= 0) {

            repathCooldown = 20;
            moveToBed();
        }
    }

    @Override
    public void stop() {

        mob.getNavigation().stop();
        targetBed = null;
    }

    private void moveToBed() {

        if (targetBed == null)
            return;

        mob.getNavigation().moveTo(
                targetBed.getX() + 0.5,
                targetBed.getY(),
                targetBed.getZ() + 0.5,
                SPEED
        );
    }
}