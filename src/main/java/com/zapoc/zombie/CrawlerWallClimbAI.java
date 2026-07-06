package com.zapoc.zombie;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CrawlerWallClimbAI {

    private static final String CLIMBING_TAG = "ZapocCrawlerClimbing";
    private static final String CLIMB_TICKS_TAG = "ZapocCrawlerClimbTicks";
    private static final String COOLDOWN_TAG = "ZapocCrawlerClimbCooldown";
    private static final String TOP_OUT_TICKS_TAG = "ZapocCrawlerTopOutTicks";
    private static final String TOP_OUT_X_TAG = "ZapocCrawlerTopOutX";
    private static final String TOP_OUT_Z_TAG = "ZapocCrawlerTopOutZ";

    private static final double CLIMB_SPEED = 0.20D;
    private static final double FORWARD_STICK_POWER = 0.04D;
    private static final double TOP_OUT_FORWARD_POWER = 0.22D;
    private static final double TOP_OUT_UP_POWER = 0.10D;
    private static final double TARGET_RADIUS = 32.0D;

    private static final int MAX_CLIMB_TICKS = 100;
    private static final int TOP_OUT_TICKS = 8;
    private static final int FAIL_COOLDOWN_TICKS = 12;

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {

        if (!(event.getEntityLiving() instanceof Mob mob))
            return;

        if (ZombieTypeManager.getType(mob) != ZombieType.CRAWLER)
            return;

        event.setCanceled(true);
        event.setDamageMultiplier(0.0F);

        mob.fallDistance = 0.0F;
        mob.setNoGravity(false);
    }

    public static void tick(Mob mob) {

        if (mob == null)
            return;

        if (!mob.isAlive())
            return;

        if (ZombieTypeManager.getType(mob) != ZombieType.CRAWLER)
            return;

        if (!(mob.level instanceof ServerLevel))
            return;

        if (mob.isInWaterOrBubble() || mob.isInLava()) {
            stopClimbing(mob, FAIL_COOLDOWN_TICKS);
            return;
        }

        tickCooldown(mob);

        if (tickTopOut(mob))
            return;

        BlockPos targetPos = getTargetPos(mob);

        if (targetPos == null) {
            stopClimbing(mob, 0);
            return;
        }

        if (targetIsBelow(mob, targetPos)) {
            stopClimbing(mob, FAIL_COOLDOWN_TICKS);
            return;
        }

        boolean climbing = mob.getPersistentData().getBoolean(CLIMBING_TAG);

        if (climbing) {
            tickClimbing(mob, targetPos);
            return;
        }

        if (mob.getPersistentData().getInt(COOLDOWN_TAG) > 0)
            return;

        if (shouldStartClimbing(mob, targetPos)) {
            startClimbing(mob);
        }
    }

    private static boolean shouldStartClimbing(Mob mob, BlockPos targetPos) {

        if (!mob.horizontalCollision)
            return false;

        return targetPos.getY() > mob.getY() + 1.0D;
    }

    private static void startClimbing(Mob mob) {

        mob.getPersistentData().putBoolean(CLIMBING_TAG, true);
        mob.getPersistentData().putInt(CLIMB_TICKS_TAG, 0);

        mob.getNavigation().stop();
        mob.setNoGravity(true);
        mob.fallDistance = 0.0F;
    }

    private static void tickClimbing(Mob mob, BlockPos targetPos) {

        mob.getNavigation().stop();
        mob.setNoGravity(true);
        mob.fallDistance = 0.0F;

        int ticks = mob.getPersistentData().getInt(CLIMB_TICKS_TAG) + 1;
        mob.getPersistentData().putInt(CLIMB_TICKS_TAG, ticks);

        if (ticks > MAX_CLIMB_TICKS) {
            stopClimbing(mob, FAIL_COOLDOWN_TICKS);
            return;
        }

        if (targetIsBelow(mob, targetPos)) {
            stopClimbing(mob, FAIL_COOLDOWN_TICKS);
            return;
        }

        if (!mob.horizontalCollision && ticks > 8) {
            startTopOut(mob, targetPos);
            return;
        }

        Vec3 toTarget = new Vec3(
                targetPos.getX() + 0.5D - mob.getX(),
                0.0D,
                targetPos.getZ() + 0.5D - mob.getZ()
        );

        Vec3 push = Vec3.ZERO;

        if (toTarget.lengthSqr() > 0.01D) {
            push = toTarget.normalize().scale(FORWARD_STICK_POWER);
        }

        Vec3 current = mob.getDeltaMovement();

        mob.setDeltaMovement(
                current.x * 0.25D + push.x,
                CLIMB_SPEED,
                current.z * 0.25D + push.z
        );
        mob.hasImpulse = true;
    }

    private static void startTopOut(Mob mob, BlockPos targetPos) {

        Vec3 toTarget = new Vec3(
                targetPos.getX() + 0.5D - mob.getX(),
                0.0D,
                targetPos.getZ() + 0.5D - mob.getZ()
        );

        if (toTarget.lengthSqr() <= 0.01D) {
            finishClimbing(mob);
            return;
        }

        Vec3 direction = toTarget.normalize();

        mob.getPersistentData().putBoolean(CLIMBING_TAG, false);
        mob.getPersistentData().remove(CLIMB_TICKS_TAG);
        mob.getPersistentData().putInt(TOP_OUT_TICKS_TAG, TOP_OUT_TICKS);
        mob.getPersistentData().putDouble(TOP_OUT_X_TAG, direction.x);
        mob.getPersistentData().putDouble(TOP_OUT_Z_TAG, direction.z);

        forceTopOut(mob, direction.x, direction.z);
    }

    private static boolean tickTopOut(Mob mob) {

        int ticks = mob.getPersistentData().getInt(TOP_OUT_TICKS_TAG);

        if (ticks <= 0)
            return false;

        double x = mob.getPersistentData().getDouble(TOP_OUT_X_TAG);
        double z = mob.getPersistentData().getDouble(TOP_OUT_Z_TAG);

        mob.getPersistentData().putInt(TOP_OUT_TICKS_TAG, ticks - 1);
        forceTopOut(mob, x, z);

        if (ticks <= 1) {
            mob.getPersistentData().remove(TOP_OUT_X_TAG);
            mob.getPersistentData().remove(TOP_OUT_Z_TAG);
            finishClimbing(mob);
        }

        return true;
    }

    private static void forceTopOut(Mob mob, double x, double z) {

        mob.getNavigation().stop();
        mob.setNoGravity(true);
        mob.fallDistance = 0.0F;

        Vec3 current = mob.getDeltaMovement();

        mob.setDeltaMovement(
                current.x * 0.15D + x * TOP_OUT_FORWARD_POWER,
                TOP_OUT_UP_POWER,
                current.z * 0.15D + z * TOP_OUT_FORWARD_POWER
        );
        mob.hasImpulse = true;
    }

    private static void finishClimbing(Mob mob) {

        mob.getPersistentData().putBoolean(CLIMBING_TAG, false);
        mob.getPersistentData().remove(CLIMB_TICKS_TAG);
        mob.getPersistentData().remove(TOP_OUT_TICKS_TAG);
        mob.getPersistentData().remove(TOP_OUT_X_TAG);
        mob.getPersistentData().remove(TOP_OUT_Z_TAG);
        mob.getPersistentData().putInt(COOLDOWN_TAG, 8);

        mob.setNoGravity(false);
        mob.fallDistance = 0.0F;
    }

    private static void stopClimbing(Mob mob, int cooldown) {

        mob.getPersistentData().putBoolean(CLIMBING_TAG, false);
        mob.getPersistentData().remove(CLIMB_TICKS_TAG);
        mob.getPersistentData().remove(TOP_OUT_TICKS_TAG);
        mob.getPersistentData().remove(TOP_OUT_X_TAG);
        mob.getPersistentData().remove(TOP_OUT_Z_TAG);

        if (cooldown > 0) {
            mob.getPersistentData().putInt(COOLDOWN_TAG, cooldown);
        }

        mob.setNoGravity(false);
        mob.fallDistance = 0.0F;
    }

    private static boolean targetIsBelow(Mob mob, BlockPos targetPos) {

        return targetPos.getY() + 0.5D < mob.getY() - 1.0D;
    }

    private static BlockPos getTargetPos(Mob mob) {

        LivingEntity target = mob.getTarget();

        if (target != null && target.isAlive()) {
            return target.blockPosition();
        }

        Player player = mob.level.getNearestPlayer(mob, TARGET_RADIUS);

        if (player != null && player.isAlive() && !player.isSpectator()) {
            return player.blockPosition();
        }

        return null;
    }

    private static void tickCooldown(Mob mob) {

        int cooldown = mob.getPersistentData().getInt(COOLDOWN_TAG);

        if (cooldown <= 0)
            return;

        mob.getPersistentData().putInt(COOLDOWN_TAG, cooldown - 1);
    }
}
