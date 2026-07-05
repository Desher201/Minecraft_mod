package com.zapoc.horde;

import com.zapoc.bed.BedManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class HordeAttackManager {

    public static void update(Mob mob) {

        if (mob == null)
            return;

        if (!HordeManager.isHordeActive())
            return;

        if (!BedManager.hasBed())
            return;

        Player player = mob.level.getNearestPlayer(mob, 20);

        if (player != null) {

            mob.setTarget(player);
            HordeGroupManager.alertGroup(mob, player);

            return;
        }

        BlockPos bed = BedManager.getBedPos();

        if (bed == null)
            return;

        mob.getNavigation().moveTo(
                bed.getX() + 0.5D,
                bed.getY(),
                bed.getZ() + 0.5D,
                1.0D
        );
    }
}