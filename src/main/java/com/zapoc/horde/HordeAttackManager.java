package com.zapoc.horde;

import com.zapoc.bed.BedManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class HordeAttackManager {

    /**
     * Главная логика поведения орды.
     */
    public static void update(Mob mob) {

        // Орда не активна
        if (!HordeManager.isHordeActive())
            return;

        // Кровати нет
        if (!BedManager.hasBed())
            return;

        // Ищем игрока рядом
        Player player = mob.level.getNearestPlayer(mob, 20);

        // Если игрок рядом — атакуем его
        if (player != null) {

            mob.setTarget(player);

            HordeGroupManager.alertGroup(mob, player);

            return;

        }

        // Игроков рядом нет
        // Продолжаем движение к кровати
        BlockPos bed = BedManager.getBedPos();

        if (bed == null)
            return;

        mob.getNavigation().moveTo(
                bed.getX() + 0.5,
                bed.getY(),
                bed.getZ() + 0.5,
                1.0
        );

    }

}
