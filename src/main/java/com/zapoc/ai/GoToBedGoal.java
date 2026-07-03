package com.zapoc.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

/**
 * Makes a mob walk toward the player-placed bed (tracked via a "player_bed"
 * tagged marker entity, the same one the zapoc datapack creates) as long as:
 *  - it is currently a judgment night (zapoc datapack scoreboard flag), and
 *  - no player is within 20 blocks (vanilla combat AI takes priority instead).
 */
public class GoToBedGoal extends Goal {

    private static final double SEARCH_RADIUS = 400.0;
    private static final double SPEED = 1.0;
    private static final double ARRIVE_DIST_SQR = 4.0;

    private final Mob mob;
    private BlockPos targetBed;
    private int recheckCooldown = 0;

    public GoToBedGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Level level = mob.level;
        if (!ZombieAIEvents.isHordeNight(level.getScoreboard())) return false;
        if (level.getNearestPlayer(mob, 20) != null) return false;

        BlockPos bed = findBed(level);
        if (bed == null) return false;

        this.targetBed = bed;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        Level level = mob.level;
        if (!ZombieAIEvents.isHordeNight(level.getScoreboard())) return false;
        if (level.getNearestPlayer(mob, 20) != null) return false;
        if (targetBed == null) return false;
        return mob.distanceToSqr(Vec3.atCenterOf(targetBed)) > ARRIVE_DIST_SQR;
    }

    @Override
    public void start() {
        if (targetBed != null) {
            mob.getNavigation().moveTo(targetBed.getX() + 0.5, targetBed.getY(), targetBed.getZ() + 0.5, SPEED);
        }
    }

    @Override
    public void tick() {
        if (targetBed == null) return;
        recheckCooldown--;
        if (recheckCooldown <= 0) {
            recheckCooldown = 20; // re-path once a second in case navigation got stuck
            if (mob.getNavigation().isDone()) {
                mob.getNavigation().moveTo(targetBed.getX() + 0.5, targetBed.getY(), targetBed.getZ() + 0.5, SPEED);
            }
        }
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
        targetBed = null;
    }

    private BlockPos findBed(Level level) {
        AABB box = mob.getBoundingBox().inflate(SEARCH_RADIUS);
        List<Marker> markers = level.getEntities(EntityType.MARKER, box,
                e -> e.getTags().contains("player_bed"));
        if (markers.isEmpty()) return null;
        return markers.get(0).blockPosition();
    }
}
