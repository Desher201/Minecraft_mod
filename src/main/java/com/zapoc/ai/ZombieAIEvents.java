package com.zapoc.ai;

import com.zapoc.horde.HordeGroupManager;
import com.zapoc.horde.HordeManager;
import com.zapoc.roaming.RoamingGroupManager;
import com.zapoc.zombie.ZombieRoleAI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber
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

        tryAddToHordeGroup(mob);
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {

        if (event.getEntityLiving().level.isClientSide())
            return;

        if (!(event.getEntityLiving() instanceof Mob mob))
            return;

        if (!isTargetMob(mob))
            return;

        ZombieRoleAI.tick(mob);

        tryAddToHordeGroup(mob);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {

        if (event.getEntityLiving().level.isClientSide())
            return;

        if (!(event.getEntityLiving() instanceof Mob mob))
            return;

        if (!isTargetMob(mob))
            return;

        if (!(event.getSource().getEntity() instanceof Player player))
            return;

        RoamingGroupManager.alertGroup(mob, player);
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

    private static void tryAddToHordeGroup(Mob mob) {

        if (!HordeManager.isHordeActive())
            return;

        if (RoamingGroupManager.isRoamingMob(mob))
            return;

        if (HordeGroupManager.isTracked(mob))
            return;

        HordeGroupManager.addZombie(mob);
    }

    private static boolean isTargetMob(Mob mob) {

        ResourceLocation id = ForgeRegistries.ENTITIES.getKey(mob.getType());

        if (id == null)
            return false;

        return TARGET_TYPES.contains(id);
    }
}
