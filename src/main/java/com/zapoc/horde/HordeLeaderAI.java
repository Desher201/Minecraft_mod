package com.zapoc.horde;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class HordeLeaderAI {

    public static void tick(HordeGroup group) {

        Mob leader = group.getLeader();

        if (leader == null)
            return;

        if (!leader.isAlive())
            return;

        // Игрок рядом
        Player player = leader.level.getNearestPlayer(leader, 20);

        if (player != null) {

            leader.setTarget(player);

            return;

        }

        // Идем к своей точке атаки
        BlockPos attackPoint = group.getAttackPoint().getPosition();

        leader.getNavigation().moveTo(
                attackPoint.getX() + 0.5,
                attackPoint.getY(),
                attackPoint.getZ() + 0.5,
                1.0
        );

    }

}
