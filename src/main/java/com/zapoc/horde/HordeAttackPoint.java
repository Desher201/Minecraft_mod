package com.zapoc.horde;

import net.minecraft.core.BlockPos;

public class HordeAttackPoint {

    private final HordeDirection direction;
    private final BlockPos position;

    public HordeAttackPoint(HordeDirection direction, BlockPos position) {

        this.direction = direction;
        this.position = position;

    }

    public HordeDirection getDirection() {
        return direction;
    }

    public BlockPos getPosition() {
        return position;
    }

}
