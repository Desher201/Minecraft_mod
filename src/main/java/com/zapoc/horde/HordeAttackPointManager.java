package com.zapoc.horde;

import com.zapoc.bed.BedManager;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class HordeAttackPointManager {

    private static final int DISTANCE = 40;

    public static List<HordeAttackPoint> getAttackPoints() {

        List<HordeAttackPoint> points = new ArrayList<>();

        if (!BedManager.hasBed())
            return points;

        BlockPos bed = BedManager.getBedPos();

        int x = bed.getX();
        int y = bed.getY();
        int z = bed.getZ();

        points.add(new HordeAttackPoint(
                HordeDirection.NORTH,
                new BlockPos(x, y, z - DISTANCE)));

        points.add(new HordeAttackPoint(
                HordeDirection.NORTH_EAST,
                new BlockPos(x + DISTANCE, y, z - DISTANCE)));

        points.add(new HordeAttackPoint(
                HordeDirection.EAST,
                new BlockPos(x + DISTANCE, y, z)));

        points.add(new HordeAttackPoint(
                HordeDirection.SOUTH_EAST,
                new BlockPos(x + DISTANCE, y, z + DISTANCE)));

        points.add(new HordeAttackPoint(
                HordeDirection.SOUTH,
                new BlockPos(x, y, z + DISTANCE)));

        points.add(new HordeAttackPoint(
                HordeDirection.SOUTH_WEST,
                new BlockPos(x - DISTANCE, y, z + DISTANCE)));

        points.add(new HordeAttackPoint(
                HordeDirection.WEST,
                new BlockPos(x - DISTANCE, y, z)));

        points.add(new HordeAttackPoint(
                HordeDirection.NORTH_WEST,
                new BlockPos(x - DISTANCE, y, z - DISTANCE)));

        return points;
    }

}