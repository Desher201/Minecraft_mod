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
import net.minecraft.world.level.pathfinder.Path;

import java.util.HashMap;
import java.util.Map;

public class HordeBlockBreakAI {

    private static final double PLAYER_BREAK_TARGET_RADIUS = 14.0D;

    private static final double START_DAMAGE_PER_SECOND = 5.0D;
    private static final double DAMAGE_PER_DAY = 0.35D;
    private static final double MAX_DAMAGE_PER_SECOND = 40.0D;

    private static final double MAX_PATH_MULTIPLIER = 1.8D;
    private static final int MAX_PATH_EXTRA_NODES = 12;
    private static final int STUCK_TICKS_REQUIRED = 40;

    private static final Map<Long, Double> BREAK_PROGRESS = new HashMap<>();
    private static final Map<String, Long> LAST_HIT_TICK = new HashMap<>();

    private static final Map<Integer, BlockPos> LAST_MOB_POS = new HashMap<>();
    private static final Map<Integer, Integer> STUCK_TICKS = new HashMap<>();

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

        if (!shouldBreakBlock(level, mob, targetPos, frontBlock))
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

        if (isSimpleStep(level, front)) {
            return null;
        }

        for (int y = 0; y <= 2; y++) {

            BlockPos checkPos = front.above(y);

            if (isWallBlock(level, checkPos)) {
                return checkPos;
            }
        }

        return null;
    }

    private static boolean shouldBreakBlock(ServerLevel level, Mob mob, BlockPos targetPos, BlockPos frontBlock) {

        if (isSimpleStep(level, frontBlock))
            return false;

        if (isMobStuck(mob))
            return true;

        return !hasGoodEnoughPath(mob, targetPos);
    }

    private static boolean hasGoodEnoughPath(Mob mob, BlockPos targetPos) {

        Path path = mob.getNavigation().createPath(targetPos, 0);

        if (path == null)
            return false;

        if (!path.canReach())
            return false;

        int directDistance = getHorizontalDistance(mob.blockPosition(), targetPos);
        int maxAllowedNodes = (int) (directDistance * MAX_PATH_MULTIPLIER) + MAX_PATH_EXTRA_NODES;

        if (maxAllowedNodes < 8)
            maxAllowedNodes = 8;

        return path.getNodeCount() <= maxAllowedNodes;
    }

    private static int getHorizontalDistance(BlockPos from, BlockPos to) {

        int dx = Math.abs(from.getX() - to.getX());
        int dz = Math.abs(from.getZ() - to.getZ());

        return dx + dz;
    }

    private static boolean isMobStuck(Mob mob) {

        int id = mob.getId();
        BlockPos currentPos = mob.blockPosition();
        BlockPos lastPos = LAST_MOB_POS.get(id);

        if (lastPos == null) {
            LAST_MOB_POS.put(id, currentPos);
            STUCK_TICKS.put(id, 0);
            return false;
        }

        if (!lastPos.equals(currentPos)) {
            LAST_MOB_POS.put(id, currentPos);
            STUCK_TICKS.put(id, 0);
            return false;
        }

        int stuckTicks = STUCK_TICKS.getOrDefault(id, 0) + 1;
        STUCK_TICKS.put(id, stuckTicks);

        return stuckTicks >= STUCK_TICKS_REQUIRED;
    }

    private static boolean isSimpleStep(ServerLevel level, BlockPos front) {

        if (!isSolidCollision(level, front))
            return false;

        if (isSolidCollision(level, front.above()))
            return false;

        if (isSolidCollision(level, front.above(2)))
            return false;

        return true;
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

        return isSolidCollision(level, pos);
    }

    private static boolean isSolidCollision(ServerLevel level, BlockPos pos) {

        BlockState state = level.getBlockState(pos);

        if (state.isAir())
            return false;

        return !state.getCollisionShape(level, pos).isEmpty();
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
        LAST_MOB_POS.clear();
        STUCK_TICKS.clear();
    }
}