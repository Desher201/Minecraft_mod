package com.zapoc.horde;

import net.minecraft.world.entity.Mob;

import java.util.ArrayList;
import java.util.List;

public class HordeGroupManager {

    private static final List<HordeGroup> GROUPS = new ArrayList<>();

    /**
     * Создать группы в начале орды.
     */
    public static void createGroups() {

        GROUPS.clear();

        List<HordeAttackPoint> points = HordeAttackPointManager.getAttackPoints();

        HordeGroupRole[] roles = {
                HordeGroupRole.ASSAULT,
                HordeGroupRole.ASSAULT,
                HordeGroupRole.FLANK,
                HordeGroupRole.FLANK,
                HordeGroupRole.BREAKER,
                HordeGroupRole.BREAKER,
                HordeGroupRole.HUNTER,
                HordeGroupRole.RESERVE
        };

        for (int i = 0; i < points.size(); i++) {

            HordeGroup group = new HordeGroup(
                    i,
                    points.get(i),
                    roles[Math.min(i, roles.length - 1)]
            );

            GROUPS.add(group);

        }
    }

    /**
     * Получить все группы.
     */
    public static List<HordeGroup> getGroups() {
        return GROUPS;
    }

    /**
     * Найти самую маленькую группу.
     */
    public static HordeGroup getSmallestGroup() {

        HordeGroup smallest = null;

        for (HordeGroup group : GROUPS) {

            if (smallest == null || group.size() < smallest.size()) {
                smallest = group;
            }

        }

        return smallest;
    }

    /**
     * Добавить зомби в группу.
     */
    public static void addZombie(Mob mob) {

        HordeGroup group = getSmallestGroup();

        if (group != null) {
            group.addZombie(mob);
        }

    }

    /**
     * Удалить зомби из группы.
     */
    public static void removeZombie(Mob mob) {

        for (HordeGroup group : GROUPS) {

            if (group.getZombies().contains(mob)) {

                group.removeZombie(mob);
                return;

            }

        }

    }

    /**
     * Очистить все группы.
     */
    public static void clear() {

        GROUPS.clear();

    }

}
