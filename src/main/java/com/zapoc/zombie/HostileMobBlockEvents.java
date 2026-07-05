package com.zapoc.zombie;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber
public class HostileMobBlockEvents {

    private static final Set<ResourceLocation> ALLOWED_HOSTILE_MOBS = new HashSet<>();

    static {

        ALLOWED_HOSTILE_MOBS.add(new ResourceLocation("minecraft", "zombie"));
        ALLOWED_HOSTILE_MOBS.add(new ResourceLocation("minecraft", "husk"));
        ALLOWED_HOSTILE_MOBS.add(new ResourceLocation("minecraft", "drowned"));
        ALLOWED_HOSTILE_MOBS.add(new ResourceLocation("minecraft", "zombie_villager"));

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
            ALLOWED_HOSTILE_MOBS.add(new ResourceLocation("zombie_extreme", id));
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {

        if (event.getWorld().isClientSide())
            return;

        ResourceLocation entityId = ForgeRegistries.ENTITIES.getKey(event.getEntity().getType());

        if (entityId == null)
            return;

        if (event.getEntity().getType().getCategory() != MobCategory.MONSTER)
            return;

        if (!ALLOWED_HOSTILE_MOBS.contains(entityId)) {
            event.setCanceled(true);
            event.getEntity().discard();
            return;
        }

        if (event.getEntity() instanceof Zombie zombie) {
            zombie.setBaby(false);
        }
    }
}