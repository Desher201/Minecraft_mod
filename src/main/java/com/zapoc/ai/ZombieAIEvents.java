package com.zapoc.ai;

import com.zapoc.horde.HordeGroupManager;
import com.zapoc.horde.HordeManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;

public class ZombieAIEvents {

    private static final Set<ResourceLocation> TARGET_TYPES = new HashSet<>();

    static {

        TARGET_TYPES.add(new ResourceLocation("minecraft", "zombie"));
        TARGET_TYPES.add(new ResourceLocation("minecraft", "husk"));
        TARGET_TYPES.add(new ResourceLocation("minecraft", "drowned"));
        TARGET_TYPES.add(new ResourceLocation("minecraft", "zombie_villager"));

        String[] zombieExtreme = {
                "assasin_black_ops",
                "bomber",
                "boomer",
                "chainsaw",
                "clicker",
                "crawler",
                "demolisher",
                "devastated",
                "faceless",
                "fetus",
                "goon",
                "infected",
                "infected_hazmat",
                "infected_juggernaut",
                "infected_military",
                "infected_police",
                "inflated",
                "military",
                "night_hunter",
                "parasite",
                "patient_zero",
                "rat_king",
                "revived",
                "runner"
        };

        for (String id : zombieExtreme) {
            TARGET_TYPES.add(new ResourceLocation("zombie_extreme", id));
        }
    }

    @SubscribeEvent
    public static void onJoin(EntityJoinWorldEvent event) {

        if (event.getWorld().isClientSide())
            return;

        if (!(event.getEntity() instanceof Mob mob))
            return;

        if (!isTargetMob(mob))
            return;

        mob.goalSelector.addGoal(2, new GoToBedGoal(mob));

        if (HordeManager.isHordeActive()) {
            HordeGroupManager.addZombie(mob);
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {

        if (event.getEntityLiving().level.isClientSide())
            return;

        if (!(event.getEntityLiving() instanceof Mob mob))
            return;

        if (!HordeManager.isHordeActive())
            return;

        if (!isTargetMob(mob))
            return;

        HordeGroupManager.addZombie(mob);
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {

        if (event.getEntityLiving().level.isClientSide())
            return;

        if (!(event.getEntityLiving() instanceof Mob mob))
            return;

        if (!isTargetMob(mob))
            return;

        HordeGroupManager.removeZombie(mob);
    }

    private static boolean isTargetMob(Mob mob) {

        ResourceLocation id = ForgeRegistries.ENTITIES.getKey(mob.getType());

        if (id == null)
            return false;

        return TARGET_TYPES.contains(id);
    }
}