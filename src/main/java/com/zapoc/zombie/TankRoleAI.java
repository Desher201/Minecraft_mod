package com.zapoc.zombie;

import com.zapoc.horde.HordeGroupManager;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public class TankRoleAI {

    private static final double SEARCH_RADIUS = 16.0D;
    private static final double DIRECT_RANGE_SQR = 4.0D * 4.0D;
    private static final double MOVE_SPEED = 0.75D;

    public static void tick(Mob mob) {

        if (HordeGroupManager.isLeader(mob))
            return;

        if (!ZombiePositioningHelper.shouldRepath(mob, "ZapocTankRepathTick", 43))
            return;

        Player player = findNearestPlayer(mob, SEARCH_RADIUS);

        if (player == null)
            return;

        mob.setTarget(player);

        if (mob.distanceToSqr(player) <= DIRECT_RANGE_SQR)
            return;

        double radius = 3.0D + (Math.abs(mob.getId()) % 5) * 0.5D;
        Vec3 pos = ZombiePositioningHelper.getSpreadPositionAroundTarget(mob, player, radius);

        mob.getNavigation().moveTo(pos.x, pos.y, pos.z, MOVE_SPEED);
    }

    private static Player findNearestPlayer(Mob mob, double radius) {

        AABB area = new AABB(mob.blockPosition()).inflate(radius);

        List<Player> players = mob.level.getEntitiesOfClass(
                Player.class,
                area,
                player -> player.isAlive() && !player.isCreative() && !player.isSpectator()
        );

        return players.stream()
                .min(Comparator.comparingDouble(mob::distanceToSqr))
                .orElse(null);
    }
}
