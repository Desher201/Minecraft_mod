package com.zapoc.horde;

import com.zapoc.bed.BedManager;
import com.zapoc.zombie.ZombieType;
import com.zapoc.zombie.ZombieTypeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class HordeBlockBreakAI {

    private static final double PLAYER_BREAK_TARGET_RADIUS = 14.0;

    private static final double START_DAMAGE_PER_SECOND = 5.0D;
    private static final double DAMAGE_PER_DAY = 0.35D;
    private static final double MAX_DAMAGE_PER_SECOND = 40.0D;
    private static final Map<Long, Double> BREAK_PROGRESS = new HashMap<>();
    private static final Map<String, Long> LAST_HIT_TICK = new HashMap<>();

    public static boolean tick(HordeGroup group) {

        if (group == null)
            return false;

        boolean breaking = false;

        for (Mob mob : group.getZombies()) {

            if (tickSingle(mob)) {
                breaking = true;
            }
        }

        return breaking;
    }

    public static boolean tickSingle(Mob mob) {

        BlockPos targetPos = getAutoBreakTarget(mob);

        return tickSingle(mob, targetPos);
    }

    public static boolean tickSingle(Mob mob, BlockPos targetPos) {

        if (mob == null)
            return false;

        if (!mob.isAlive())
            return false;

        if (ZombieTypeManager.getType(mob) != ZombieType.BREAKER)
            return false;

        if (!(mob.level instanceof ServerLevel level))
            return false;

        if (targetPos == null)
            return false;

        BlockPos frontBlock = getFrontWallBlock(level, mob, targetPos);

        if (frontBlock == null)
            return false;

        mob.getNavigation().stop();

        breakWall3x3(level, mob, frontBlock, targetPos);

        return true;
    }

    private static BlockPos getAutoBreakTarget(Mob mob) {

        if (mob == null)
            return null;

        LivingEntity currentTarget = mob.getTarget();

        if (currentTarget != null && currentTarget.isAlive()) {
            return currentTarget.blockPosition();
        }

        Player nearbyPlayer = mob.level.getNearestPlayer(
                mob,
                PLAYER_BREAK_TARGET_RADIUS
        );

        if (nearbyPlayer != null && nearbyPlayer.isAlive()) {
            return nearbyPlayer.blockPosition();
        }

        if (BedManager.hasBed() && mob.level instanceof ServerLevel level) {

            if (level.dimension().equals(BedManager.getDimension())) {
                return BedManager.getBedPos();
            }
        }

        return null;
    }

    private static BlockPos getFrontWallBlock(ServerLevel level, Mob mob, BlockPos targetPos) {

        Direction direction = getDirectionToTarget(mob.blockPosition(), targetPos);

        BlockPos mobPos = mob.blockPosition();

        BlockPos front = mobPos.relative(direction, 1);

        for (int y = 0; y <= 2; y++) {

            BlockPos checkPos = front.above(y);

            if (isWallBlock(level, checkPos)) {
                return checkPos;
            }
        }

        return null;
    }

    private static void breakWall3x3(ServerLevel level, Mob breaker, BlockPos frontBlock, BlockPos targetPos) {

        Direction direction = getDirectionToTarget(breaker.blockPosition(), targetPos);

        int baseY = breaker.blockPosition().getY();

        for (int side = -1; side <= 1; side++) {

            for (int y = 0; y <= 2; y++) {

                BlockPos pos;

                if (direction == Direction.NORTH || direction == Direction.SOUTH) {

                    pos = new BlockPos(
                            frontBlock.getX() + side,
                            baseY + y,
                            frontBlock.getZ()
                    );

                } else {

                    pos = new BlockPos(
                            frontBlock.getX(),
                            baseY + y,
                            frontBlock.getZ() + side
                    );
                }

                damageBlock(level, breaker, pos);
            }
        }
    }

    private static void damageBlock(ServerLevel level, Mob breaker, BlockPos pos) {

        if (!isWallBlock(level, pos))
            return;

        int requiredProgress = BlockLevelSystem.getRequiredProgress(level, pos);

        if (requiredProgress <= 0)
            return;

        long gameTime = level.getGameTime();
        String hitKey = breaker.getId() + ":" + pos.asLong();

        Long lastHitTick = LAST_HIT_TICK.get(hitKey);

        if (lastHitTick != null && lastHitTick == gameTime)
            return;

        LAST_HIT_TICK.put(hitKey, gameTime);

        long blockKey = pos.asLong();

        double damage = getBlockDamagePerTick();
        double progress = BREAK_PROGRESS.getOrDefault(blockKey, 0.0D);

        progress += damage;

        breaker.swing(InteractionHand.MAIN_HAND);

        level.levelEvent(
                2001,
                pos,
                Block.getId(level.getBlockState(pos))
        );

        if (progress >= requiredProgress) {

            level.destroyBlock(pos, true);
            BREAK_PROGRESS.remove(blockKey);
            LAST_HIT_TICK.remove(hitKey);

        } else {

            BREAK_PROGRESS.put(blockKey, progress);
        }
    }

    private static boolean isWallBlock(ServerLevel level, BlockPos pos) {

        BlockState state = level.getBlockState(pos);

        if (state.isAir())
            return false;

        if (!BlockLevelSystem.canBreakerBreak(level, pos))
            return false;

        if (state.getCollisionShape(level, pos).isEmpty())
            return false;

        return true;
    }

    private static double getBlockDamagePerTick() {

        return getBlockDamagePerSecond() / 20.0D;
    }

    private static double getBlockDamagePerSecond() {

        int day = HordeManager.getCurrentDay();

        if (day < 1)
            day = 1;

        double damage = START_DAMAGE_PER_SECOND + ((day - 1) * DAMAGE_PER_DAY);

        if (damage > MAX_DAMAGE_PER_SECOND)
            return MAX_DAMAGE_PER_SECOND;

        return damage;
    }

    private static Direction getDirectionToTarget(BlockPos from, BlockPos target) {

        int dx = target.getX() - from.getX();
        int dz = target.getZ() - from.getZ();

        if (Math.abs(dx) > Math.abs(dz)) {
            return dx > 0 ? Direction.EAST : Direction.WEST;
        }

        return dz > 0 ? Direction.SOUTH : Direction.NORTH;
    }

    public static void reset() {
        BREAK_PROGRESS.clear();
        LAST_HIT_TICK.clear();
    }
}