package com.zapoc.horde;

import com.zapoc.bed.BedChunkLoader;
import com.zapoc.bed.BedManager;
import com.zapoc.bed.BedPersistenceManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class HordeBedAttackAI {

    private static final double ATTACK_RADIUS = 2.8;
    private static final double HELPER_RADIUS = 3.5;

    private static final int BREAK_PROGRESS_REQUIRED = 80;

    private static final Map<Integer, Integer> BREAK_PROGRESS = new HashMap<>();

    public static boolean tick(HordeGroup group) {

        if (group == null)
            return false;

        Mob leader = group.getLeader();

        if (leader == null)
            return false;

        if (!leader.isAlive())
            return false;

        if (!BedManager.hasBed())
            return false;

        if (!(leader.level instanceof ServerLevel level))
            return false;

        if (!level.dimension().equals(BedManager.getDimension()))
            return false;

        BlockPos bedPos = BedManager.getBedPos();

        if (bedPos == null)
            return false;

        BlockState bedState = level.getBlockState(bedPos);

        if (!(bedState.getBlock() instanceof BedBlock)) {

            BREAK_PROGRESS.remove(group.getId());
            return false;
        }

        if (!canMobReachBed(level, leader, bedPos)) {

            BREAK_PROGRESS.remove(group.getId());
            return false;
        }

        clearTargets(group);

        int attackers = countAttackers(level, group, bedPos);

        if (attackers <= 0) {

            BREAK_PROGRESS.remove(group.getId());
            return false;
        }

        int progress = BREAK_PROGRESS.getOrDefault(group.getId(), 0);

        progress += attackers;

        BREAK_PROGRESS.put(group.getId(), progress);

        swingAttackers(level, group, bedPos);

        if (progress >= BREAK_PROGRESS_REQUIRED) {

            destroyBed(level, bedPos, bedState);

            BREAK_PROGRESS.remove(group.getId());

            BedChunkLoader.unloadChunks(level);
            BedManager.removeBed();
            BedManager.setHardcore(true);
            BedPersistenceManager.saveBed(level);

            System.out.println("[ZApoc] Horde destroyed the global bed.");

            return true;
        }

        return true;
    }

    private static int countAttackers(ServerLevel level, HordeGroup group, BlockPos bedPos) {

        int count = 0;

        for (Mob mob : group.getZombies()) {

            if (mob == null)
                continue;

            if (!mob.isAlive())
                continue;

            if (canMobHelpAttackBed(level, mob, bedPos)) {
                count++;
            }
        }

        return count;
    }

    private static void swingAttackers(ServerLevel level, HordeGroup group, BlockPos bedPos) {

        for (Mob mob : group.getZombies()) {

            if (mob == null)
                continue;

            if (!mob.isAlive())
                continue;

            if (canMobHelpAttackBed(level, mob, bedPos)) {
                mob.swing(InteractionHand.MAIN_HAND);
            }
        }
    }

    private static boolean canMobHelpAttackBed(ServerLevel level, Mob mob, BlockPos bedPos) {

        double distance = mob.distanceToSqr(Vec3.atCenterOf(bedPos));

        if (distance > HELPER_RADIUS * HELPER_RADIUS)
            return false;

        return hasClearLineToBed(level, mob, bedPos);
    }

    private static boolean canMobReachBed(ServerLevel level, Mob mob, BlockPos bedPos) {

        double distance = mob.distanceToSqr(Vec3.atCenterOf(bedPos));

        if (distance > ATTACK_RADIUS * ATTACK_RADIUS)
            return false;

        return hasClearLineToBed(level, mob, bedPos);
    }

    private static boolean hasClearLineToBed(ServerLevel level, Mob mob, BlockPos bedPos) {

        Vec3 start = mob.getEyePosition(1.0F);
        Vec3 end = Vec3.atCenterOf(bedPos);

        BlockHitResult hit = level.clip(
                new ClipContext(
                        start,
                        end,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        mob
                )
        );

        if (hit.getType() != HitResult.Type.BLOCK)
            return false;

        BlockPos hitPos = hit.getBlockPos();

        if (hitPos.equals(bedPos))
            return true;

        return level.getBlockState(hitPos).getBlock() instanceof BedBlock;
    }

    private static void clearTargets(HordeGroup group) {

        for (Mob mob : group.getZombies()) {

            if (mob == null)
                continue;

            if (!mob.isAlive())
                continue;

            mob.setTarget(null);
        }
    }

    private static void destroyBed(ServerLevel level, BlockPos bedPos, BlockState bedState) {

        if (!(bedState.getBlock() instanceof BedBlock)) {
            level.destroyBlock(bedPos, true);
            return;
        }

        Direction facing = bedState.getValue(BedBlock.FACING);
        BedPart part = bedState.getValue(BedBlock.PART);

        BlockPos otherPartPos;

        if (part == BedPart.HEAD) {
            otherPartPos = bedPos.relative(facing.getOpposite());
        } else {
            otherPartPos = bedPos.relative(facing);
        }

        BlockState otherState = level.getBlockState(otherPartPos);

        level.destroyBlock(bedPos, true);

        if (otherState.getBlock() instanceof BedBlock) {
            level.destroyBlock(otherPartPos, true);
        }
    }

    public static void reset() {
        BREAK_PROGRESS.clear();
    }
}