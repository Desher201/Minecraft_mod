package com.zapoc.zombie;

import com.zapoc.horde.HordeGroupManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ZombiePositioningHelper {

    public static Vec3 getSpreadPositionAroundTarget(Mob mob, LivingEntity target, double radius) {

        double angle = getStableAngle(mob, 80);
        double x = target.getX() + Math.cos(angle) * radius;
        double z = target.getZ() + Math.sin(angle) * radius;

        return adjustToOpenPosition(mob.level, x, target.getY(), z, target.position());
    }

    public static Vec3 getSidePressurePosition(Mob mob, LivingEntity target, double radius) {

        double dx = target.getX() - mob.getX();
        double dz = target.getZ() - mob.getZ();
        double length = Math.sqrt(dx * dx + dz * dz);

        if (length < 0.001D)
            return getSpreadPositionAroundTarget(mob, target, radius);

        double side = ((mob.getId() + mob.tickCount / 100) & 1) == 0 ? 1.0D : -1.0D;
        double variation = (((mob.getId() * 31) % 100) / 100.0D - 0.5D) * 1.5D;
        double sideX = (-dz / length) * (radius + variation) * side;
        double sideZ = (dx / length) * (radius + variation) * side;

        return adjustToOpenPosition(
                mob.level,
                target.getX() + sideX,
                target.getY(),
                target.getZ() + sideZ,
                target.position()
        );
    }

    public static boolean shouldRepath(Mob mob, String cooldownTag, int interval) {

        int nextTick = mob.getPersistentData().getInt(cooldownTag);

        if (mob.tickCount < nextTick)
            return false;

        int offset = Math.abs(mob.getId()) % Math.max(1, interval / 2);
        mob.getPersistentData().putInt(cooldownTag, mob.tickCount + interval + offset);

        return true;
    }

    public static void applySoftSeparation(Mob mob) {

        if (HordeGroupManager.isLeader(mob))
            return;

        AABB area = mob.getBoundingBox().inflate(1.2D, 0.3D, 1.2D);
        List<Mob> mobs = mob.level.getEntitiesOfClass(
                Mob.class,
                area,
                other -> other != mob && other.isAlive() && !other.isRemoved()
        );

        if (mobs.isEmpty())
            return;

        double pushX = 0.0D;
        double pushZ = 0.0D;

        for (Mob other : mobs) {
            double dx = mob.getX() - other.getX();
            double dz = mob.getZ() - other.getZ();
            double distanceSqr = dx * dx + dz * dz;

            if (distanceSqr < 0.0001D || distanceSqr > 1.44D)
                continue;

            double distance = Math.sqrt(distanceSqr);
            pushX += dx / distance;
            pushZ += dz / distance;
        }

        double length = Math.sqrt(pushX * pushX + pushZ * pushZ);

        if (length < 0.001D)
            return;

        double strength = 0.04D;
        mob.push((pushX / length) * strength, 0.0D, (pushZ / length) * strength);
        mob.hasImpulse = true;
    }

    private static double getStableAngle(Mob mob, int variationTicks) {

        int baseDegrees = Math.floorMod(mob.getId() * 47, 360);
        int timeDegrees = Math.floorMod((mob.tickCount / variationTicks) * 23, 360);

        return Math.toRadians(baseDegrees + timeDegrees);
    }

    private static Vec3 adjustToOpenPosition(Level level, double x, double y, double z, Vec3 fallback) {

        BlockPos base = new BlockPos(x, y, z);

        for (int dy = 0; dy <= 2; dy++) {
            BlockPos pos = base.above(dy);

            if (isOpenForMob(level, pos)) {
                return new Vec3(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
            }
        }

        for (int dy = 1; dy <= 2; dy++) {
            BlockPos pos = base.below(dy);

            if (isOpenForMob(level, pos)) {
                return new Vec3(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
            }
        }

        return fallback;
    }

    private static boolean isOpenForMob(Level level, BlockPos pos) {

        if (!level.getBlockState(pos).getCollisionShape(level, pos).isEmpty())
            return false;

        if (!level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty())
            return false;

        return !level.getBlockState(pos.below()).getCollisionShape(level, pos.below()).isEmpty();
    }
}
