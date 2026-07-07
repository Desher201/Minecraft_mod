package com.zapoc.horde;

import com.zapoc.bed.BedChunkLoader;
import com.zapoc.bed.BedManager;
import com.zapoc.bed.BedPersistenceManager;
import com.zapoc.message.ApocalypseMessageManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HordeBedAttackAI {

    private static final double HELPER_RADIUS = 4.0D;
    private static final int BREAK_PROGRESS_REQUIRED = 80;
    private static final int REMOVE_BLOCK_FLAGS = 34;

    private static final Map<Integer, Integer> BREAK_PROGRESS = new HashMap<>();

    public static boolean tick(HordeGroup group) {

        if (group == null)
            return false;

        if (!BedManager.hasBed())
            return false;

        BlockPos bedPos = BedManager.getBedPos();

        if (bedPos == null)
            return false;

        ServerLevel level = getGroupLevel(group);

        if (level == null)
            return false;

        if (!level.dimension().equals(BedManager.getDimension()))
            return false;

        BlockState bedState = level.getBlockState(bedPos);

        if (!(bedState.getBlock() instanceof BedBlock)) {
            BREAK_PROGRESS.remove(group.getId());
            return false;
        }

        int attackers = countAttackers(level, group, bedPos, bedState);

        if (attackers <= 0) {
            BREAK_PROGRESS.remove(group.getId());
            return false;
        }

        clearTargets(group);

        int progress = BREAK_PROGRESS.getOrDefault(group.getId(), 0);
        progress += attackers;

        BREAK_PROGRESS.put(group.getId(), progress);

        swingAttackers(level, group, bedPos, bedState);

        if (progress >= BREAK_PROGRESS_REQUIRED) {

            destroyBedWithoutDrop(level, bedPos, bedState);

            BREAK_PROGRESS.remove(group.getId());

            BedChunkLoader.unloadChunks(level);
            BedManager.removeBed();
            BedManager.setHardcore(true);
            BedPersistenceManager.saveBed(level);
            ApocalypseMessageManager.sendBedDestroyedMessage(level);

            System.out.println("[ZApoc] Horde destroyed the global bed.");

            return true;
        }

        return true;
    }

    private static ServerLevel getGroupLevel(HordeGroup group) {

        List<Mob> zombies = getZombiesSnapshot(group);

        for (Mob mob : zombies) {

            if (mob == null)
                continue;

            if (!mob.isAlive())
                continue;

            if (mob.level instanceof ServerLevel level) {
                return level;
            }
        }

        return null;
    }

    private static int countAttackers(ServerLevel level, HordeGroup group, BlockPos bedPos, BlockState bedState) {

        int count = 0;

        List<Mob> zombies = getZombiesSnapshot(group);

        for (Mob mob : zombies) {

            if (mob == null)
                continue;

            if (!mob.isAlive())
                continue;

            if (canMobAttackBed(level, mob, bedPos, bedState)) {
                count++;
            }
        }

        return count;
    }

    private static void swingAttackers(ServerLevel level, HordeGroup group, BlockPos bedPos, BlockState bedState) {

        List<Mob> zombies = getZombiesSnapshot(group);

        for (Mob mob : zombies) {

            if (mob == null)
                continue;

            if (!mob.isAlive())
                continue;

            if (canMobAttackBed(level, mob, bedPos, bedState)) {
                mob.swing(InteractionHand.MAIN_HAND);
            }
        }
    }

    private static boolean canMobAttackBed(ServerLevel level, Mob mob, BlockPos bedPos, BlockState bedState) {

        if (mob.distanceToSqr(
                bedPos.getX() + 0.5D,
                bedPos.getY() + 0.5D,
                bedPos.getZ() + 0.5D
        ) > HELPER_RADIUS * HELPER_RADIUS) {
            return false;
        }

        BlockPos mobPos = mob.blockPosition();
        Vec3 mobReachPos = mob.position().add(0.0D, mob.getEyeHeight() * 0.75D, 0.0D);

        if (canReachBedPart(level, mobPos, mobReachPos, bedPos))
            return true;

        BlockPos otherPartPos = getOtherBedPartPos(bedPos, bedState);
        BlockState otherState = level.getBlockState(otherPartPos);

        if (!(otherState.getBlock() instanceof BedBlock))
            return false;

        return canReachBedPart(level, mobPos, mobReachPos, otherPartPos);
    }

    private static boolean canReachBedPart(ServerLevel level, BlockPos mobPos, Vec3 mobReachPos, BlockPos bedPartPos) {

        int dx = Math.abs(mobPos.getX() - bedPartPos.getX());
        int dy = Math.abs(mobPos.getY() - bedPartPos.getY());
        int dz = Math.abs(mobPos.getZ() - bedPartPos.getZ());

        if (dy > 2)
            return false;

        if (dx > 2 || dz > 2)
            return false;

        return hasLineOfSightToBedPart(level, mobReachPos, bedPartPos);
    }

    private static boolean hasLineOfSightToBedPart(ServerLevel level, Vec3 from, BlockPos bedPartPos) {

        Vec3 to = Vec3.atCenterOf(bedPartPos);

        BlockHitResult hit = level.clip(new ClipContext(
                from,
                to,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                null
        ));

        if (hit.getType() == HitResult.Type.MISS)
            return true;

        return hit.getBlockPos().equals(bedPartPos);
    }

    private static void clearTargets(HordeGroup group) {

        List<Mob> zombies = getZombiesSnapshot(group);

        for (Mob mob : zombies) {

            if (mob == null)
                continue;

            if (!mob.isAlive())
                continue;

            mob.setTarget(null);
        }
    }

    private static List<Mob> getZombiesSnapshot(HordeGroup group) {

        if (group == null)
            return new ArrayList<>();

        return new ArrayList<>(group.getZombies());
    }

    private static void destroyBedWithoutDrop(ServerLevel level, BlockPos bedPos, BlockState bedState) {

        BlockPos otherPartPos = getOtherBedPartPos(bedPos, bedState);
        BlockState otherState = level.getBlockState(otherPartPos);

        playBreakEffect(level, bedPos, bedState);

        if (otherState.getBlock() instanceof BedBlock) {
            playBreakEffect(level, otherPartPos, otherState);
        }

        if (otherState.getBlock() instanceof BedBlock) {
            level.setBlock(otherPartPos, Blocks.AIR.defaultBlockState(), REMOVE_BLOCK_FLAGS);
        }

        level.setBlock(bedPos, Blocks.AIR.defaultBlockState(), REMOVE_BLOCK_FLAGS);
    }

    private static BlockPos getOtherBedPartPos(BlockPos bedPos, BlockState bedState) {

        if (!(bedState.getBlock() instanceof BedBlock))
            return bedPos;

        Direction facing = bedState.getValue(BedBlock.FACING);
        BedPart part = bedState.getValue(BedBlock.PART);

        if (part == BedPart.HEAD) {
            return bedPos.relative(facing.getOpposite());
        }

        return bedPos.relative(facing);
    }

    private static void playBreakEffect(ServerLevel level, BlockPos pos, BlockState state) {

        level.levelEvent(
                2001,
                pos,
                Block.getId(state)
        );
    }

    public static void reset() {
        BREAK_PROGRESS.clear();
    }
}
