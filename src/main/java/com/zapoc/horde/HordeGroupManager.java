package com.zapoc.horde;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class HordeGroupManager {

    private static final List<HordeGroup> GROUPS = new ArrayList<>();

    private static final double ALERT_RADIUS = 40.0;

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

    public static List<HordeGroup> getGroups() {
        return GROUPS;
    }

    public static HordeGroup getSmallestGroup() {

        HordeGroup smallest = null;

        for (HordeGroup group : GROUPS) {

            if (smallest == null || group.size() < smallest.size()) {
                smallest = group;
            }
        }

        return smallest;
    }

    public static void addZombie(Mob mob) {

        if (mob == null)
            return;

        if (!isAllowedHordeMob(mob))
            return;

        if (isTracked(mob))
            return;

        HordeGroup group = getSmallestGroup();

        if (group != null) {
            group.addZombie(mob);
        }
    }

    public static void removeZombie(Mob mob) {

        if (mob == null)
            return;

        for (HordeGroup group : GROUPS) {

            if (group.getZombies().contains(mob)) {
                group.removeZombie(mob);
                return;
            }
        }
    }

    public static boolean isTracked(Mob mob) {

        for (HordeGroup group : GROUPS) {

            if (group.getZombies().contains(mob)) {
                return true;
            }
        }

        return false;
    }

    public static void alertGroup(Mob caller, LivingEntity target) {

        if (caller == null || target == null)
            return;

        List<Mob> nearbyMobs = caller.level.getEntitiesOfClass(
                Mob.class,
                new AABB(caller.blockPosition()).inflate(ALERT_RADIUS)
        );

        for (Mob mob : nearbyMobs) {

            if (mob.isRemoved())
                continue;

            if (!mob.isAlive())
                continue;

            if (!isAllowedHordeMob(mob))
                continue;

            mob.setTarget(target);

            if (HordeManager.isHordeActive()) {
                addZombie(mob);
            }
        }
    }

    public static void tickGroups() {

        if (!HordeManager.isHordeActive())
            return;

        for (HordeGroup group : GROUPS) {
            HordeGroupAI.tick(group);
        }
    }

    public static void cleanupDeadZombies() {

        for (HordeGroup group : GROUPS) {

            List<Mob> copy = new ArrayList<>(group.getZombies());

            for (Mob mob : copy) {

                if (mob == null || mob.isRemoved() || !mob.isAlive()) {
                    group.removeZombie(mob);
                }
            }
        }
    }

    private static boolean isAllowedHordeMob(Mob mob) {

        ResourceLocation id = ForgeRegistries.ENTITIES.getKey(mob.getType());

        if (id == null)
            return false;

        String namespace = id.getNamespace();
        String path = id.getPath();

        if (namespace.equals("minecraft")) {

            return path.equals("zombie")
                    || path.equals("husk")
                    || path.equals("drowned")
                    || path.equals("zombie_villager");
        }

        return namespace.equals("zombie_extreme");
    }

    public static void clear() {

        GROUPS.clear();
        HordeBedAttackAI.reset();
    }
}