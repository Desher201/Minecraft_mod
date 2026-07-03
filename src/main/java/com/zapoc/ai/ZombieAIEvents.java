package com.zapoc.ai;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;

public class ZombieAIEvents {

    // Every entity type that should get the "go to bed" AI during judgment nights.
    private static final Set<ResourceLocation> TARGET_TYPES = new HashSet<>();

    static {
        // vanilla undead
        TARGET_TYPES.add(new ResourceLocation("minecraft", "zombie"));
        TARGET_TYPES.add(new ResourceLocation("minecraft", "husk"));
        TARGET_TYPES.add(new ResourceLocation("minecraft", "drowned"));
        TARGET_TYPES.add(new ResourceLocation("minecraft", "zombie_villager"));

        // Zombie Extreme mobs (registry names pulled directly from the jar)
        String[] zombieExtreme = {
                "assasin_black_ops", "bomber", "boomer", "chainsaw", "clicker", "crawler",
                "demolisher", "devastated", "faceless", "fetus", "goon", "infected",
                "infected_hazmat", "infected_juggernaut", "infected_military", "infected_police",
                "inflated", "military", "night_hunter", "parasite", "patient_zero", "rat_king",
                "revived", "runner"
        };
        for (String id : zombieExtreme) {
            TARGET_TYPES.add(new ResourceLocation("zombie_extreme", id));
        }
    }

    @SubscribeEvent
    public static void onJoin(EntityJoinWorldEvent event) {
        if (event.getWorld().isClientSide()) return;
        if (!(event.getEntity() instanceof Mob mob)) return;

        ResourceLocation id = ForgeRegistries.ENTITIES.getKey(mob.getType());
        if (id == null || !TARGET_TYPES.contains(id)) return;

        // Priority 2: lower priority than melee attack / swim goals, higher than idle wander.
        mob.goalSelector.addGoal(2, new GoToBedGoal(mob));
    }

    /** Reads the "#horde_night" flag that the zapoc datapack maintains every tick. */
    public static boolean isHordeNight(Scoreboard scoreboard) {
        return getGlobalScore(scoreboard, "#horde_night") >= 1;
    }

    public static int getGlobalScore(Scoreboard scoreboard, String holder) {
        Objective obj = scoreboard.getObjective("zapoc.g");
        if (obj == null) return 0;
        if (!scoreboard.hasPlayerScore(holder, obj)) return 0;
        return scoreboard.getOrCreatePlayerScore(holder, obj).getScore();
    }
}
