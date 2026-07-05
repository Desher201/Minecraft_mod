package com.zapoc.ai;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;

public class ZombieAIEvents {

    // Все типы зомби, которым добавляется AI орды
    private static final Set<ResourceLocation> TARGET_TYPES = new HashSet<>();

    static {

        // Vanilla
        TARGET_TYPES.add(new ResourceLocation("minecraft", "zombie"));
        TARGET_TYPES.add(new ResourceLocation("minecraft", "husk"));
        TARGET_TYPES.add(new ResourceLocation("minecraft", "drowned"));
        TARGET_TYPES.add(new ResourceLocation("minecraft", "zombie_villager"));

        // Zombie Extreme
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

        ResourceLocation id = ForgeRegistries.ENTITIES.getKey(mob.getType());

        if (id == null)
            return;

        if (!TARGET_TYPES.contains(id))
            return;

        // Добавляем AI орды
        mob.goalSelector.addGoal(2, new GoToBedGoal(mob));

    }

}