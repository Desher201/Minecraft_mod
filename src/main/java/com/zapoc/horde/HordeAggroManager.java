package com.zapoc.horde;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class HordeAggroManager {

    private static final double ALERT_RADIUS = 40.0;

    public static void update(Mob mob) {

        if (!HordeManager.isHordeActive())
            return;

        Player player = mob.level.getNearestPlayer(mob, 20);

        if (player == null)
            return;

        List<Mob> zombies = mob.level.getEntitiesOfClass(
                Mob.class,
                new AABB(mob.blockPosition()).inflate(ALERT_RADIUS)
        );

        for (Mob zombie : zombies) {

            if (zombie.isRemoved())
                continue;

            zombie.setTarget(player);

        }

    }

}
