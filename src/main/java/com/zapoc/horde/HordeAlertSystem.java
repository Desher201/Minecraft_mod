package com.zapoc.horde;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class HordeAlertSystem {

    private static final double ALERT_RADIUS = 32.0;

    public static void alertNearbyZombies(Mob caller, Player target) {

        List<Mob> mobs = caller.level.getEntitiesOfClass(
                Mob.class,
                new AABB(caller.blockPosition()).inflate(ALERT_RADIUS)
        );

        for (Mob mob : mobs) {

            if (mob == caller)
                continue;

            if (mob.getTarget() != null)
                continue;

            mob.setTarget(target);

        }

    }

}
